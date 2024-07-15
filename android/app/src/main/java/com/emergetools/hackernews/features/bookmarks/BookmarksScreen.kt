package com.emergetools.hackernews.features.bookmarks

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.emergetools.hackernews.features.comments.CommentsDestinations
import com.emergetools.hackernews.features.stories.StoriesDestinations
import com.emergetools.hackernews.features.stories.StoryRow
import com.emergetools.hackernews.ui.theme.HackerNewsTheme

@Composable
fun BookmarksScreen(
  state: BookmarksState,
  actions: (BookmarksAction) -> Unit,
  navigator: (BookmarksNavigation) -> Unit,
) {
  LazyColumn {
    items(items = state.bookmarks, key = { it.id }) { item ->
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
    }
  }
}

@Preview
@Composable
private fun BookmarksScreenPreview() {
  HackerNewsTheme {
    BookmarksScreen(
      state = BookmarksState(),
      actions = {},
      navigator = {}
    )
  }
}