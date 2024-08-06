package com.emergetools.hackernews.features.settings

import android.net.Uri
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.emergetools.hackernews.data.LocalCustomTabsIntent
import com.emergetools.hackernews.userStorage
import kotlinx.serialization.Serializable

sealed interface SettingsDestinations {
  @Serializable
  data object Settings : SettingsDestinations
}

fun NavGraphBuilder.settingsRoutes(navController: NavController) {
  composable<SettingsDestinations.Settings> {
    val context = LocalContext.current
    val intent = LocalCustomTabsIntent.current
    val model = viewModel<SettingsViewModel>(
      factory = SettingsViewModel.Factory(
        userStorage = context.userStorage()
      )
    )
    val state by model.state.collectAsState()
    SettingsScreen(
      state = state,
      actions = model::actions,
      navigation = { place ->
        when (place) {
          is SettingsNavigation.GoToLogin -> {
            navController.navigate(place.login)
          }

          is SettingsNavigation.GoToSettingsLink -> {
            intent.launchUrl(context, Uri.parse(place.url))
          }
        }
      }
    )
  }
}
