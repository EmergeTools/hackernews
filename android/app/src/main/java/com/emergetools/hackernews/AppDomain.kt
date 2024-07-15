package com.emergetools.hackernews

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDestination
import com.emergetools.hackernews.features.bookmarks.BookmarksDestinations.Bookmarks
import com.emergetools.hackernews.features.settings.SettingsDestinations.Settings
import com.emergetools.hackernews.features.stories.Stories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class NavItem(
  @DrawableRes
  val icon: Int,
  val label: String,
  val route: Any,
  val selected: Boolean,
)

data class AppState(
  val navItems: List<NavItem> = listOf(
    NavItem(
      icon = R.drawable.ic_feed,
      label = "Feed",
      route = Stories,
      selected = true
    ),
    NavItem(
      icon = R.drawable.ic_bookmarks,
      label = "Bookmarks",
      route = Bookmarks,
      selected = false
    ),
    NavItem(
      icon = R.drawable.ic_settings,
      label = "Settings",
      route = Settings,
      selected = false
    ),
  )
) {
  val selectedItem = navItems.first { it.selected }
  val topLevelRoutes = navItems.associateBy { it.route.javaClass.simpleName }
}

sealed interface AppAction {
  data class NavItemSelected(val item: NavItem) : AppAction
  data class DestinationChanged(val destination: NavDestination) : AppAction
}

class AppViewModel : ViewModel() {
  private val internalState = MutableStateFlow(AppState())
  val state = internalState.asStateFlow()

  fun actions(action: AppAction) {
    when (action) {
      is AppAction.NavItemSelected -> {
        if (action.item != internalState.value.selectedItem) {
          internalState.update { current ->
            current.copy(
              navItems = current.navItems.map { item ->
                if (action.item == item) {
                  item.copy(selected = true)
                } else {
                  item.copy(selected = false)
                }
              }
            )
          }
        }
      }

      is AppAction.DestinationChanged -> {
        // TODO: figure out a better way to sync the current destination with bottom nav
        val currentState = internalState.value
        val parent = action.destination.parent?.route
        val route = parent ?: action.destination.route
        currentState.topLevelRoutes.keys.forEach { key ->
          if (route != null && route.contains(key)) {
            val item = currentState.topLevelRoutes[key]
            if (item != currentState.selectedItem) {
              // select bottom nav
              internalState.value = currentState.copy(
                navItems = currentState.navItems.map { navItem ->
                  if (item == navItem) {
                    navItem.copy(selected = true)
                  } else {
                    navItem.copy(selected = false)
                  }
                }
              )
            }
          }
        }
      }
    }
  }
}