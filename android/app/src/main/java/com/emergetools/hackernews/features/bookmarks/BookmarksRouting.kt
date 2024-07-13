package com.emergetools.hackernews.features.bookmarks

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.emergetools.hackernews.features.bookmarks.BookmarksDestinations.Bookmarks
import kotlinx.serialization.Serializable

sealed interface BookmarksDestinations {
  @Serializable
  data object Bookmarks: BookmarksDestinations
}

fun NavGraphBuilder.bookmarksRoutes() {
  composable<Bookmarks> {
    BookmarksScreen()
  }
}