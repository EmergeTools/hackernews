package com.emergetools.hackernews.features.stories

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emergetools.hackernews.data.Item
import com.emergetools.hackernews.data.ItemRepository
import com.emergetools.hackernews.data.Page
import com.emergetools.hackernews.data.next
import com.emergetools.hackernews.data.relativeTimeStamp
import com.emergetools.hackernews.features.comments.CommentsDestinations
import com.emergetools.hackernews.features.stories.StoriesAction.LoadItems
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FeedType(val label: String) {
  Top("Top"),
  New("New")
}

enum class LoadingState {
  Loading,
  LoadingPage,
  Refreshing,
  Idle
}

data class StoriesState(
  val stories: List<StoryItem>,
  val feed: FeedType = FeedType.Top,
  val loading: LoadingState = LoadingState.Idle,
)

sealed class StoryItem(open val id: Long) {
  data class Loading(override val id: Long) : StoryItem(id)
  data class Content(
    override val id: Long,
    val title: String,
    val author: String,
    val score: Int,
    val commentCount: Int,
    val timeLabel: String,
    val url: String?
  ) : StoryItem(id)
}

sealed class StoriesAction {
  data object LoadItems : StoriesAction()
  data object LoadNextPage : StoriesAction()
  data object RefreshItems: StoriesAction()
  data class SelectStory(val id: Long) : StoriesAction()
  data class SelectComments(val id: Long) : StoriesAction()
  data class SelectFeed(val feed: FeedType) : StoriesAction()
}

sealed interface StoriesNavigation {
  data class GoToStory(val closeup: StoriesDestinations.Closeup) : StoriesNavigation
  data class GoToComments(val comments: CommentsDestinations.Comments) : StoriesNavigation
}

class StoriesViewModel(private val itemRepository: ItemRepository) : ViewModel() {
  private val internalState = MutableStateFlow(StoriesState(stories = emptyList()))
  val state = internalState.asStateFlow()

  // TODO: decide if this should be in the ViewModel or the Repository
  private val pages = mutableListOf<Page>()
  private var fetchJob: Job? = null

  init {
    actions(LoadItems)
  }

  fun actions(action: StoriesAction) {
    when (action) {
      LoadItems -> {
        pages.clear()
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
          pages.addAll(
            itemRepository
              .getFeedIds(internalState.value.feed)
              .chunked(FEED_PAGE_SIZE)
          )
          val page = pages.next()

          val newStories = fetchPage(page) {
            internalState.update { current ->
              current.copy(
                stories = page.map { StoryItem.Loading(it) },
                loading = LoadingState.Loading
              )
            }
          }

          internalState.update { current ->
            current.copy(
              stories = newStories,
              loading = LoadingState.Idle
            )
          }
        }
      }

      is StoriesAction.RefreshItems -> {
        if (internalState.value.loading != LoadingState.Idle) {
          return
        }
        fetchJob?.cancel()
        pages.clear()
        fetchJob = viewModelScope.launch {
          pages.addAll(
            itemRepository
              .getFeedIds(internalState.value.feed)
              .chunked(FEED_PAGE_SIZE)
          )
          val page = pages.next()
          val newStories = fetchPage(page) {
            internalState.update { current ->
              current.copy(
                loading = LoadingState.Refreshing
              )
            }
          }
          internalState.update { current ->
            current.copy(
              stories = newStories,
              loading = LoadingState.Idle
            )
          }
        }
      }

      is StoriesAction.SelectStory -> {
        // TODO
      }

      is StoriesAction.SelectComments -> {
        // TODO
      }

      is StoriesAction.SelectFeed -> {
        if (action.feed != state.value.feed) {
          internalState.update { current ->
            current.copy(
              feed = action.feed,
              stories = emptyList()
            )
          }
          actions(LoadItems)
        }
      }

      StoriesAction.LoadNextPage -> {
        if (pages.isNotEmpty() && internalState.value.loading == LoadingState.Idle) {
          fetchJob = viewModelScope.launch {
            val page = pages.next()
            Log.d("Feed", "Loading next page: $page")
            val newStories = fetchPage(page) {
              internalState.update { current ->
                current.copy(loading = LoadingState.LoadingPage)
              }
            }

            internalState.update { current ->
              current.copy(
                stories = current.stories.subList(0, current.stories.lastIndex) + newStories,
                loading = LoadingState.Idle
              )
            }
          }
        }
      }
    }
  }

  private suspend fun fetchPage(page: Page, onLoading: () -> Unit = {}): List<StoryItem>  {
    onLoading()
    var newStories = itemRepository
      .getPage(page)
      .map<Item, StoryItem> { item ->
        Log.d("Feed", "Item: $item")
        StoryItem.Content(
          id = item.id,
          title = item.title!!,
          author = item.by!!,
          score = item.score ?: 0,
          commentCount = item.descendants ?: 0,
          timeLabel = relativeTimeStamp(epochSeconds = item.time),
          url = item.url
        )
      }
    if (pages.isNotEmpty()) {
      newStories = newStories + StoryItem.Loading(0L)
    }
    return newStories
  }

  @Suppress("UNCHECKED_CAST")
  class Factory(private val itemRepository: ItemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return StoriesViewModel(itemRepository) as T
    }
  }
}

const val FEED_PAGE_SIZE = 20
