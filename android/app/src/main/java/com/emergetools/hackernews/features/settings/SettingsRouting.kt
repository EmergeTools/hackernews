package com.emergetools.hackernews.features.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

sealed interface SettingsDestinations {
  @Serializable
  data object Settings: SettingsDestinations
}

fun NavGraphBuilder.settingsRoutes() {
  composable<SettingsDestinations.Settings> {
    SettingsScreen()
  }
}