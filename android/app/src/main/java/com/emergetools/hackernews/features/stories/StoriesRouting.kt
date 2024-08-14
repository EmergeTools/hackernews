package com.emergetools.hackernews.features.stories

import android.net.Uri
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.emergetools.hackernews.LocalCustomTabsIntent
import com.emergetools.hackernews.baseClient
import com.emergetools.hackernews.bookmarkDao
import com.emergetools.hackernews.features.stories.StoriesDestinations.Closeup
import com.emergetools.hackernews.features.stories.StoriesDestinations.Feed
import kotlinx.serialization.Serializable

@Serializable
data object Stories

sealed interface StoriesDestinations {
  @Serializable
  data object Feed : StoriesDestinations

  @Serializable
  data class Closeup(val url: String) : StoriesDestinations
}

fun NavGraphBuilder.storiesGraph(navController: NavController) {
  navigation<Stories>(startDestination = Feed) {
    composable<Feed> {
      val context = LocalContext.current
      val intent = LocalCustomTabsIntent.current

      val model = viewModel<StoriesViewModel>(
        factory = StoriesViewModel.Factory(
          baseClient = context.baseClient(),
          bookmarkDao = context.bookmarkDao()
        )
      )
      val state by model.state.collectAsState()

      StoriesScreen(
        state = state,
        actions = model::actions,
        navigation = { place ->
          when (place) {
            is StoriesNavigation.GoToComments -> {
              navController.navigate(place.comments)
            }

            is StoriesNavigation.GoToStory -> {
              intent.launchUrl(context, Uri.parse(place.closeup.url))
            }
          }
        }
      )
    }
    composable<Closeup> { entry ->
      val closeup: Closeup = entry.toRoute()
      StoryScreen(closeup.url)
    }
  }
}
