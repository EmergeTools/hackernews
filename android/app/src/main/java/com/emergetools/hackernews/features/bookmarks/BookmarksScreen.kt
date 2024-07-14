package com.emergetools.hackernews.features.bookmarks

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.emergetools.hackernews.features.stories.StoryRow
import com.emergetools.hackernews.ui.theme.HackerNewsTheme

@Composable
fun BookmarksScreen(
  state: BookmarksState,
  actions: (BookmarksAction) -> Unit
) {
  LazyColumn {
    items(items = state.bookmarks, key = { it.id }) { item ->
      StoryRow(
        item = item,
        onClick = {},
        onBookmark = { actions(BookmarksAction.RemoveBookmark(it)) },
        onCommentClicked = {}
      )
    }
  }
}

@Preview
@Composable
private fun BookmarksScreenPreview() {
  HackerNewsTheme {
    BookmarksScreen(
      state = BookmarksState(),
      actions = {}
    )
  }
}