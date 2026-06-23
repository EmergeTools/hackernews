package com.emergetools.hackernews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.emergetools.hackernews.features.bookmarks.bookmarksRoutes
import com.emergetools.hackernews.features.comments.commentsRoutes
import com.emergetools.hackernews.features.login.loginRoutes
import com.emergetools.hackernews.features.settings.settingsRoutes
import com.emergetools.hackernews.features.stories.Stories
import com.emergetools.hackernews.features.stories.StoriesDestinations.Feed
import com.emergetools.hackernews.features.stories.storiesGraph
import com.emergetools.hackernews.ui.components.ChromeTabsProvider
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
  vararg navigators: Navigator<out NavDestination>,
  onDestinationChanged: (NavDestination) -> Unit
): NavHostController {
  return rememberNavController(*navigators).apply {
    addOnDestinationChangedListener { _, destination, _ ->
      onDestinationChanged(destination)
    }
  }
}

@Composable
fun App() {
  val model = viewModel<AppViewModel>()
  val state by model.state.collectAsState()
  val navController = rememberNavController { destination ->
    model.actions(AppAction.DestinationChanged(destination))
  }

  Scaffold(
    bottomBar = {
      NavigationBar {
        state.navItems.forEach { navItem ->
          NavigationBarItem(
            selected = navItem.selected,
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = MaterialTheme.colorScheme.primary,
              indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            ),
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
    },
    contentWindowInsets = WindowInsets.safeContent
  ) { innerPadding ->
    NavHost(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      navController = navController,
      enterTransition = { scaleIn(initialScale = 1.05f) + fadeIn() },
      exitTransition = { fadeOut() },
      popEnterTransition = { scaleIn(initialScale = 0.95f) + fadeIn() },
      startDestination = Stories
    ) {
      storiesGraph(navController)
      commentsRoutes(navController)
      bookmarksRoutes(navController)
      settingsRoutes(navController)
      loginRoutes(navController)
    }
  }
}
