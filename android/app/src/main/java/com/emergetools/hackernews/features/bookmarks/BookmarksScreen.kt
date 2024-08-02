package com.emergetools.hackernews.features.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.features.comments.CommentsDestinations
import com.emergetools.hackernews.features.stories.ListSeparator
import com.emergetools.hackernews.features.stories.StoriesDestinations
import com.emergetools.hackernews.features.stories.StoryItem
import com.emergetools.hackernews.features.stories.StoryRow
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
      style = MaterialTheme.typography.titleMedium
    )
    LazyColumn {
      itemsIndexed(items = state.bookmarks, key = {_, item -> item.id }) { index, item ->
        StoryRow(
          item = item,
          onClick = {
            if (it.url != null){
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
        if(index != state.bookmarks.lastIndex) {
          ListSeparator(
            lineColor = MaterialTheme.colorScheme.surfaceContainerHighest,
          )
        }
      }
    }
  }
}

@Preview
@Composable
fun BookmarksScreenPreview() {
  HackerNewsTheme {
    BookmarksScreen(
      state = BookmarksState(
        bookmarks = listOf(
          StoryItem.Content(
            id = 1L,
            title = "Hello There",
            author = "newuser",
            score = 10,
            commentCount = 0,
            epochTimestamp = 100L,
            timeLabel = "2h ago",
            bookmarked = true,
            url = ""
          ),
          StoryItem.Content(
            id = 2L,
            title = "Show HN: A new Android client",
            author = "heyrikin",
            score = 10,
            commentCount = 45,
            epochTimestamp = 100L,
            timeLabel = "3h ago",
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