package com.emergetools.hackernews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.emergetools.hackernews.data.ChromeTabsProvider
import com.emergetools.hackernews.data.LocalCustomTabsIntent
import com.emergetools.hackernews.features.bookmarks.BookmarksNavigation
import com.emergetools.hackernews.features.bookmarks.bookmarksRoutes
import com.emergetools.hackernews.features.comments.commentsRoutes
import com.emergetools.hackernews.features.settings.settingsRoutes
import com.emergetools.hackernews.features.stories.Stories
import com.emergetools.hackernews.features.stories.StoriesDestinations.Feed
import com.emergetools.hackernews.features.stories.storiesGraph
import com.emergetools.hackernews.ui.theme.HackerNewsTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      HackerNewsTheme {
        ChromeTabsProvider {
          App()
        }
      }
    }
  }
}

@Composable
fun rememberNavController(
  onDestinationChanged: (NavDestination) -> Unit
): NavHostController {
  return rememberNavController().apply {
    addOnDestinationChangedListener { _, destination, _ ->
      onDestinationChanged(destination)
    }
  }
}

@Composable
fun App() {
  val model = viewModel<AppViewModel>()
  val state by model.state.collectAsState()
  val navController = rememberNavController() { destination ->
    model.actions(AppAction.DestinationChanged(destination))
  }
  Scaffold(
    bottomBar = {
      NavigationBar {
        state.navItems.forEach { navItem ->
          NavigationBarItem(
            selected = navItem.selected,
            onClick = {
              model.actions(AppAction.NavItemSelected(navItem))

              navController.navigate(navItem.route) {
                popUpTo<Feed> {
                  saveState = true
                }
                launchSingleTop = true
                restoreState = true
              }
            },
            icon = {
              Icon(
                painter = painterResource(navItem.icon),
                contentDescription = navItem.label
              )
            },
          )
        }
      }
    }
  ) { innerPadding ->
    NavHost(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      navController = navController,
      enterTransition = { slideIn { IntOffset(x = it.width, y = 0) } },
      exitTransition = { slideOut { IntOffset(x = -it.width / 3, y = 0) } + fadeOut() },
      popEnterTransition = { slideIn { IntOffset(x = -it.width, y = 0) } },
      popExitTransition = { slideOut { IntOffset(x = it.width, y = 0) } },
      startDestination = Stories
    ) {
      storiesGraph(navController)
      commentsRoutes()
      bookmarksRoutes(navController)
      settingsRoutes()
    }
  }
}
