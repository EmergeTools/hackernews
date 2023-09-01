package com.emergetools.hackernews.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.emergetools.hackernews.ui.comments.CommentsScreen
import com.emergetools.hackernews.ui.stories.StoriesScreen
import com.emergetools.hackernews.ui.story.StoryScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

sealed class Screen(val route: String) {
  object Auth : Screen("auth")
  object Stories : Screen("stories")
  object Story : Screen("story/{id}") {

    fun getRoute(id: Long) = "story/$id"
  }

  object Comments : Screen("comments/{id}") {
    fun getRoute(id: Long) = "comments/$id"
  }
}

@ExperimentalAnimationApi
@Composable
fun HNNavHost() {
  val navController = rememberAnimatedNavController()

  // TODO: Deep link for handling comments
  AnimatedNavHost(
    navController = navController,
    startDestination = Screen.Stories.route,
  ) {
    composable(
      Screen.Stories.route,
      enterTransition = {
        slideIntoContainer(
          AnimatedContentScope.SlideDirection.Left,
          animationSpec = tween(300)
        )
      },
      exitTransition = {
        slideOutOfContainer(
          AnimatedContentScope.SlideDirection.Left,
          animationSpec = tween(300)
        )
      },
      popEnterTransition = {
        slideIntoContainer(
          AnimatedContentScope.SlideDirection.Right,
          animationSpec = tween(300)
        )
      },
      popExitTransition = {
        slideOutOfContainer(
          AnimatedContentScope.SlideDirection.Right,
          animationSpec = tween(300)
        )
      }) {
      StoriesScreen(navController = navController)
    }
    composable(
      Screen.Story.route,
      enterTransition = {
        slideIntoContainer(
          AnimatedContentScope.SlideDirection.Left,
          animationSpec = tween(300)
        )
      },
      exitTransition = {
        slideOutOfContainer(
          AnimatedContentScope.SlideDirection.Left,
          animationSpec = tween(300)
        )
      },
      popEnterTransition = {
        slideIntoContainer(
          AnimatedContentScope.SlideDirection.Right,
          animationSpec = tween(300)
        )
      },
      popExitTransition = {
        slideOutOfContainer(
          AnimatedContentScope.SlideDirection.Right,
          animationSpec = tween(300)
        )
      },
      arguments = listOf(
        navArgument("id") { type = NavType.LongType }
      )
    ) {
      val id = it.arguments?.getLong("id")
      requireNotNull(id) { "No argument found for id launching story screen" }
      StoryScreen(
        navController = navController,
        id = id,
      )
    }
    composable(
      Screen.Comments.route,
      enterTransition = {
        slideIntoContainer(
          AnimatedContentScope.SlideDirection.Left,
          animationSpec = tween(300)
        )
      },
      exitTransition = {
        slideOutOfContainer(
          AnimatedContentScope.SlideDirection.Left,
          animationSpec = tween(300)
        )
      },
      popEnterTransition = {
        slideIntoContainer(
          AnimatedContentScope.SlideDirection.Right,
          animationSpec = tween(300)
        )
      },
      popExitTransition = {
        slideOutOfContainer(
          AnimatedContentScope.SlideDirection.Right,
          animationSpec = tween(300)
        )
      },
      arguments = listOf(
        navArgument("id") { type = NavType.LongType }
      )) {
      val id = it.arguments?.getLong("id")
      requireNotNull(id) { "No argument found for id launching comments screen" }
      CommentsScreen(
        navController = navController,
        id = id,
      )
    }
  }
}
