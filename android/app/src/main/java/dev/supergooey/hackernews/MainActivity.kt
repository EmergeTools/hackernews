package dev.supergooey.hackernews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.supergooey.hackernews.features.comments.CommentsDestinations
import dev.supergooey.hackernews.features.comments.commentsRoutes
import dev.supergooey.hackernews.features.stories.Stories
import dev.supergooey.hackernews.features.stories.storiesGraph
import dev.supergooey.hackernews.ui.theme.HackerNewsTheme

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

  Scaffold() { innerPadding ->
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
