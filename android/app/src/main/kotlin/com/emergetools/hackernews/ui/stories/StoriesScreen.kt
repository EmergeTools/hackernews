package com.emergetools.hackernews.ui.stories

import android.util.Log
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksActivityViewModel
import com.emergetools.hackernews.R
import com.emergetools.hackernews.network.HNApi
import com.emergetools.hackernews.network.HNApiService
import com.emergetools.hackernews.network.models.Item
import com.emergetools.hackernews.network.models.Story
import com.emergetools.hackernews.ui.BuildItem
import com.emergetools.hackernews.ui.Orange
import com.emergetools.hackernews.ui.Screen
import com.emergetools.hackernews.utils.forEachInParallel

@Composable
fun StoriesScreen(navController: NavController) {
  val storiesViewModel: StoriesViewModel = mavericksActivityViewModel()

  val state by storiesViewModel.collectAsState()
  val stories = state.stories.values.toList()

  val listState = rememberLazyListState()

  Scaffold(
    topBar = {
      StoriesToolbar(
        refreshAction = storiesViewModel::fetchTopStories,
      )
    }
  ) {
    LazyColumn(state = listState) {
      itemsIndexed(stories) { index, item ->
        Log.d("item", "item.id: ${item.id}")
        BuildItem(
          index = index,
          item = item,
          onItemClick = {
            Log.d("StoriesScreen", "id: ${it.id}")
            when (it) {
              is Story -> navController.navigate(Screen.Story.getRoute(it.id))
              else -> TODO()
            }
          },
          onItemPrimaryButtonClick = {
            when (it) {
              is Story -> navController.navigate(Screen.Comments.getRoute(it.id))
              else -> TODO()
            }
          }
        )

        if (index == stories.lastIndex) {
          storiesViewModel.fetchAdditionalStories()
        }
      }

      if (state.isLoading) {
        item {
          BoxWithConstraints(
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp)
          ) {
            CircularProgressIndicator(
              modifier = Modifier
                .align(Alignment.Center)
            )
          }
        }
      }
    }
  }
}

@Composable
fun StoriesToolbar(
  refreshAction: () -> Unit,
) {
  TopAppBar(
    backgroundColor = Orange,
    title = { Text(stringResource(R.string.app_name), color = Color.White) },
    actions = {
      var expanded by remember { mutableStateOf(false) }

      // TODO: Set spinning if currently refreshing
      IconButton(onClick = refreshAction) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = stringResource(R.string.refresh),
          tint = Color.White
        )
      }

      IconButton(onClick = { expanded = !expanded }) {
        Icon(
          imageVector = Icons.Default.MoreVert,
          contentDescription = stringResource(R.string.more),
          tint = Color.White
        )
      }
    }
  )
}

data class StoriesState(
  val topStoriesResponse: Async<List<Long>> = Uninitialized,
  val storyResponse: Async<Item> = Uninitialized,
  val stories: Map<Long, Item> = emptyMap(),
  val start: Int = 0,
) : MavericksState {
  val isLoading = !storyResponse.complete || !topStoriesResponse.complete
}

class StoriesViewModel(
  initialState: StoriesState,
  private val api: HNApiService,
) : MavericksViewModel<StoriesState>(initialState) {

  init {
    fetchTopStories()

    onAsync(StoriesState::topStoriesResponse) {
      setState {
        copy(stories = emptyMap())
      }

      withState { state ->
        it.subList(state.start, state.start + OFFSET).forEach(::fetchStory)
      }
    }

    onEach(StoriesState::start) { start ->
      withState { state ->
        val topStories = state.topStoriesResponse()
        val allStoriesCount = topStories?.size ?: return@withState
        // Fetch stories up to the current size + OFFSET,
        // or the total top stories count if near/past the end.
        val end = if (start + OFFSET > allStoriesCount) {
          if (start >= allStoriesCount) return@withState
          allStoriesCount
        } else start + OFFSET

        topStories.subList(start, end).forEachInParallel(::fetchStory)
      }
    }
  }

  fun fetchTopStories() {
    suspend { api.getTopStories() }.execute { response ->
      copy(topStoriesResponse = response)
    }
  }

  fun fetchAdditionalStories() = setState {
    copy(start = stories.size)
  }

  private fun fetchStory(id: Long) {
    suspend { api.getItem(id) }.execute { response ->
      val updatedStories = stories.toMutableMap()
      response()?.let { updatedStories.putIfAbsent(it.id, it) }
      copy(
        storyResponse = response,
        stories = updatedStories,
      )
    }
  }

  companion object : MavericksViewModelFactory<StoriesViewModel, StoriesState> {

    const val OFFSET = 20

    @JvmStatic
    override fun create(
      viewModelContext: ViewModelContext,
      state: StoriesState,
    ) = StoriesViewModel(
      state,
      HNApi.retrofitService,
    )
  }
}
