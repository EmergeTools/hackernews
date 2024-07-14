package com.emergetools.hackernews.features.bookmarks

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.emergetools.hackernews.bookmarkDao
import com.emergetools.hackernews.features.bookmarks.BookmarksDestinations.Bookmarks
import kotlinx.serialization.Serializable

sealed interface BookmarksDestinations {
  @Serializable
  data object Bookmarks: BookmarksDestinations
}

fun NavGraphBuilder.bookmarksRoutes() {
  composable<Bookmarks> {
    val context = LocalContext.current
    val model = viewModel<BookmarksViewModel>(
      factory = BookmarksViewModel.Factory(
        bookmarkDao = context.bookmarkDao()
      )
    )
    val state by model.state.collectAsState()
    BookmarksScreen(
      state = state,
      actions = model::actions
    )
  }
}