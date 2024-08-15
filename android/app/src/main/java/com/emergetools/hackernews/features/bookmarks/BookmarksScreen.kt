package com.emergetools.hackernews.features.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.features.bookmarks.components.BookmarksEducationCard
import com.emergetools.hackernews.features.comments.CommentsDestinations
import com.emergetools.hackernews.features.stories.StoriesDestinations
import com.emergetools.hackernews.features.stories.StoryItem
import com.emergetools.hackernews.ui.components.ColumnSeparator
import com.emergetools.hackernews.ui.components.StoryRow
import com.emergetools.hackernews.ui.preview.SnapshotPreview
import com.emergetools.hackernews.ui.theme.HackerNewsTheme

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
      Box(modifier = Modifier
        .fillMaxWidth()
        .weight(1f), contentAlignment = Alignment.Center) {
        BookmarksEducationCard()
      }
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
            ColumnSeparator(
              lineColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
          }
        }
      }
    }
  }
}

@SnapshotPreview
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
            bookmarked = true,
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
            bookmarked = true,
            url = ""
          ),
        )
      ),
      actions = {},
      navigator = {}
    )
  }
}

@SnapshotPreview
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

