package com.emergetools.hackernews.ui.comments

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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksActivityViewModel
import com.emergetools.hackernews.R
import com.emergetools.hackernews.network.models.Story
import com.emergetools.hackernews.ui.BuildItem
import com.emergetools.hackernews.ui.Orange
import com.emergetools.hackernews.ui.Screen
import com.emergetools.hackernews.ui.stories.StoriesViewModel

@Composable
fun CommentsScreen(
  navController: NavController,
  id: Long,
) {
  val storiesViewModel: StoriesViewModel = mavericksActivityViewModel()
  val state by storiesViewModel.collectAsState()
  val story = state.stories[id]?.let {
    it as? Story
      ?: throw IllegalArgumentException("item $id not type Story, type: ${it.javaClass.simpleName}")
  } ?: throw IllegalArgumentException("item $id not found in stories map")

  val comments = state.comments.values.toList()
  val listState = rememberLazyListState()
  storiesViewModel.fetchComments(id);

  Scaffold(
    topBar = {
      TopAppBar(
        backgroundColor = Orange,
        title = {
          Text(
            text = story.title,
            color = Color.White,
            style = MaterialTheme.typography.body1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        },
        navigationIcon = {
          IconButton(onClick = navController::popBackStack) {
            Icon(
              imageVector = Icons.Filled.ArrowBack,
              contentDescription = stringResource(R.string.content_description_back),
            )
          }
        },
        actions = {
          IconButton(onClick = { navController.navigate(Screen.Story.getRoute(id)) }) {
            Icon(
              imageVector = Icons.AutoMirrored.Default.TextSnippet,
              contentDescription = stringResource(R.string.content_description_story_button),
              tint = Color.White
            )
          }
        }
      )
    }
  ) {
    LazyColumn(state = listState) {
      itemsIndexed(comments) { index, item ->
        BuildItem(
          index = index,
          item = item,
          onItemClick = {
            Log.d("Comment onItemClick", "id: ${it.id}")
          },
          onItemPrimaryButtonClick = {
            Log.d("Comment onItemPrimaryButtonClick", "id: ${it.id}")
          }
        )
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
