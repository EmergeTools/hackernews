package com.emergetools.hackernews.features.comments

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.emergetools.hackernews.searchClient
import kotlinx.serialization.Serializable

sealed interface CommentsDestinations {
  @Serializable
  data class Comments(val storyId: Long) : CommentsDestinations
}

fun NavGraphBuilder.commentsRoutes() {
  composable<CommentsDestinations.Comments> { entry ->
    val context = LocalContext.current
    val comments: CommentsDestinations.Comments = entry.toRoute()
    val model = viewModel<CommentsViewModel>(
      factory = CommentsViewModel.Factory(
        itemId = comments.storyId,
        searchClient = context.searchClient()
      )
    )
    val state by model.state.collectAsState()
    CommentsScreen(state)
  }
}




