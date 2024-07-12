package com.emergetools.hackernews.features.stories

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.emergetools.hackernews.features.stories.StoriesDestinations.Closeup
import com.emergetools.hackernews.features.stories.StoriesDestinations.Feed
import com.emergetools.hackernews.itemRepository
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
      val customTabsIntent = remember {
        CustomTabsIntent.Builder().build()
      }

      val model = viewModel<StoriesViewModel>(
        factory = StoriesViewModel.Factory(
          itemRepository = context.itemRepository()
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
              customTabsIntent.launchUrl(context, Uri.parse(place.closeup.url))
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