package com.emergetools.hackernews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.emergetools.hackernews.features.comments.commentsRoutes
import com.emergetools.hackernews.features.stories.Stories
import com.emergetools.hackernews.features.stories.storiesGraph
import com.emergetools.hackernews.ui.theme.HNOrange
import com.emergetools.hackernews.ui.theme.HNOrangeLight
import com.emergetools.hackernews.ui.theme.HackerNewsTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      HackerNewsTheme {
        App()
      }
    }
  }
}

@Composable
fun App() {
  val navController = rememberNavController()

  Scaffold(
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
      ) {
        NavigationBarItem(
          selected = true,
          onClick = {},
          icon = {
            Icon(
              imageVector = Icons.Rounded.Menu,
              contentDescription = "Feed"
            )
          },
          colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = MaterialTheme.colorScheme.onSurface,
            selectedIconColor = MaterialTheme.colorScheme.onSurface,
            indicatorColor = HNOrangeLight
          )
        )
        NavigationBarItem(
          selected = false,
          onClick = {},
          icon = {
            Icon(
              imageVector = Icons.Rounded.Settings,
              contentDescription = "Settings"
            )
          }
        )
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
    }
  }
}
