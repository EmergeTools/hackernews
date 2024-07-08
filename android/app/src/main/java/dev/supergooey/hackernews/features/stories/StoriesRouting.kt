package dev.supergooey.hackernews.features.stories

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import dev.supergooey.baseClient
import dev.supergooey.hackernews.features.stories.StoriesDestinations.Closeup
import dev.supergooey.hackernews.features.stories.StoriesDestinations.Feed
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
          baseClient = context.baseClient()
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
//              navController.navigate(place.closeup)
              customTabsIntent.launchUrl(context, Uri.parse(place.closeup.url))
            }
          }
        }
      )
    }
    composable<Closeup> { entry ->
      val closeup: Closeup =  entry.toRoute()
      StoryScreen(closeup.url)
    }
  }
}