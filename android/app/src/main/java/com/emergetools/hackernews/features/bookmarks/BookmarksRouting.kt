package com.emergetools.hackernews.features.bookmarks

import android.net.Uri
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.emergetools.hackernews.bookmarkDao
import com.emergetools.hackernews.features.bookmarks.BookmarksDestinations.Bookmarks
import com.emergetools.hackernews.ui.components.LocalCustomTabsIntent
import kotlinx.serialization.Serializable

sealed interface BookmarksDestinations {
  @Serializable
  data object Bookmarks : BookmarksDestinations
}

fun NavGraphBuilder.bookmarksRoutes(
  navController: NavController
) {
  composable<Bookmarks> {
    val context = LocalContext.current
    val intent = LocalCustomTabsIntent.current
    val model = viewModel<BookmarksViewModel>(
      factory = BookmarksViewModel.Factory(
        bookmarkDao = context.bookmarkDao()
      )
    )
    val state by model.state.collectAsState()
    BookmarksScreen(
      state = state,
      actions = model::actions,
      navigator = { place ->
        when (place) {
          is BookmarksNavigation.GoToComments -> {
            navController.navigate(place.comments)
          }

          is BookmarksNavigation.GoToStory -> {
            intent.launchUrl(context, Uri.parse(place.closeup.url))
          }
        }
      }
    )
  }
}
