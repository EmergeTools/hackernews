package com.emergetools.hackernews.features.comments

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.emergetools.hackernews.searchClient
import com.emergetools.hackernews.userStorage
import com.emergetools.hackernews.webClient
import kotlinx.serialization.Serializable

sealed interface CommentsDestinations {
  @Serializable
  data class Comments(val storyId: Long) : CommentsDestinations
}

fun NavGraphBuilder.commentsRoutes(navController: NavController) {
  composable<CommentsDestinations.Comments> { entry ->
    val context = LocalContext.current
    val comments: CommentsDestinations.Comments = entry.toRoute()
    val model = viewModel<CommentsViewModel>(
      factory = CommentsViewModel.Factory(
        itemId = comments.storyId,
        searchClient = context.searchClient(),
        webClient = context.webClient(),
        userStorage = context.userStorage()
      )
    )
    val state by model.state.collectAsState()
    CommentsScreen(
      state = state,
      actions = model::actions,
      navigation = { place ->
        when (place) {
          is CommentsNavigation.GoToLogin -> {
            navController.navigate(place.route)
          }
        }
      }
    )
  }
}




