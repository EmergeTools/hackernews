package com.emergetools.hackernews.features.bookmarks

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.features.comments.CommentsDestinations
import com.emergetools.hackernews.features.stories.ListSeparator
import com.emergetools.hackernews.features.stories.StoriesDestinations
import com.emergetools.hackernews.features.stories.StoryItem
import com.emergetools.hackernews.features.stories.StoryRow
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BookmarksScreen(
  state: BookmarksState,
  actions: (BookmarksAction) -> Unit,
  navigator: (BookmarksNavigation) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(color = MaterialTheme.colorScheme.background),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Text(
      text = "Bookmarks",
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      color = MaterialTheme.colorScheme.onBackground,
      style = MaterialTheme.typography.titleMedium
    )
    if (state.bookmarks.isEmpty()) {
      BookmarksEmptyState()
    } else {
      LazyColumn {
        itemsIndexed(items = state.bookmarks, key = { _, item -> item.id }) { index, item ->
          StoryRow(
            item = item,
            onClick = {
              if (it.url != null) {
                navigator(BookmarksNavigation.GoToStory(StoriesDestinations.Closeup(it.url)))
              } else {
                navigator(BookmarksNavigation.GoToComments(CommentsDestinations.Comments(it.id)))
              }
            },
            onBookmark = { actions(BookmarksAction.RemoveBookmark(it)) },
            onCommentClicked = {
              navigator(BookmarksNavigation.GoToComments(CommentsDestinations.Comments(it.id)))
            }
          )
          if (index != state.bookmarks.lastIndex) {
            ListSeparator(
              lineColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
          }
        }
      }
    }
  }
}

@PreviewLightDark
@Composable
fun BookmarksScreenPreview() {
  HackerNewsTheme {
    BookmarksScreen(
      state = BookmarksState(
        bookmarks = listOf(
          StoryItem.Content(
            id = 1L,
            title = "Show HN: A new Android client",
            author = "heyrikin",
            score = 10,
            commentCount = 45,
            epochTimestamp = 100L,
            timeLabel = "3h ago",
            bookmarked = true,
            url = ""
          ),
          StoryItem.Content(
            id = 2L,
            title = "Can we stop the decline of monarch butterflies and other pollinators?",
            author = "rbro112",
            score = 40,
            commentCount = 23,
            epochTimestamp = 100L,
            timeLabel = "2h ago",
            url = ""
          ),
          StoryItem.Content(
            id = 3L,
            title = "Andy Warhol's lost Amiga art found",
            author = "telkins",
            score = 332,
            commentCount = 103,
            epochTimestamp = 100L,
            timeLabel = "7h ago",
            url = ""
          ),
        )
      ),
      actions = {},
      navigator = {}
    )
  }
}

@PreviewLightDark
@Composable
fun BookmarksScreenEmptyPreview() {
  HackerNewsTheme {
    BookmarksScreen(
      state = BookmarksState(
        bookmarks = emptyList()
      ),
      actions = {},
      navigator = {}
    )
  }
}

@Composable
private fun ColumnScope.BookmarksEmptyState() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .weight(1f),
    verticalArrangement = Arrangement.spacedBy(
      space = 8.dp,
      alignment = Alignment.CenterVertically
    ),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    val scale = remember { Animatable(0.8f) }
    var bookmarked by remember { mutableStateOf(false) }
    val nubRadius = remember { Animatable(30f) }
    val nubTranslation = remember { Animatable(0.1f) }
    val nubColor = MaterialTheme.colorScheme.onBackground.copy(0.2f)
    LaunchedEffect(Unit) {
      while (true) {
        delay(3000)
        nubTranslation.animateTo(0.3f)
        launch {
          scale.animateTo(0.7f)
        }
        launch {
          nubRadius.animateTo(20f)
        }
        delay(700)
        bookmarked = true
        delay(500)
        launch {
          scale.animateTo(0.8f)
        }
        launch {
          nubRadius.animateTo(30f)
        }
        delay(200)
        nubTranslation.animateTo(0.1f)
        delay(3000)
        nubTranslation.animateTo(0.3f)
        launch {
          scale.animateTo(0.7f)
        }
        launch {
          nubRadius.animateTo(20f)
        }
        delay(700)
        bookmarked = false
        delay(500)
        launch {
          scale.animateTo(0.8f)
        }
        launch {
          nubRadius.animateTo(30f)
        }
        delay(200)
        nubTranslation.animateTo(0.1f)
      }
    }
    Text(
      modifier = Modifier.wrapContentSize(),
      text = "Long press a story to bookmark it...",
      color = MaterialTheme.colorScheme.onBackground,
      style = MaterialTheme.typography.labelMedium,
      fontWeight = FontWeight.Medium
    )
    StoryRow(
      modifier = Modifier
        .drawWithContent {
          drawContent()

          val x = size.width * 0.8f
          val y = size.height * nubTranslation.value
          drawCircle(
            color = nubColor,
            center = Offset(x, y),
            radius = nubRadius.value
          )
          drawCircle(
            color = nubColor,
            center = Offset(x, y),
            radius = 30f
          )
        }
        .scale(scale.value)
        .border(
          width = 1.dp,
          color = MaterialTheme.colorScheme.onBackground,
          shape = RoundedCornerShape(8.dp)
        ),
      item = StoryItem.Content(
        id = 1L,
        title = "Show HN: A new Android client",
        author = "heyrikin",
        score = 10,
        commentCount = 45,
        epochTimestamp = 100L,
        timeLabel = "3h ago",
        bookmarked = bookmarked,
        url = ""
      ),
      onClick = {},
      onBookmark = {},
      onCommentClicked = {}
    )
    Text(
      modifier = Modifier.wrapContentSize(),
      text = "...and save it for later",
      color = MaterialTheme.colorScheme.onBackground,
      style = MaterialTheme.typography.labelMedium,
      fontWeight = FontWeight.Medium
    )
  }
}
