package com.emergetools.hackernews.ui.story

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksActivityViewModel
import com.emergetools.hackernews.R
import com.emergetools.hackernews.network.models.Story
import com.emergetools.hackernews.ui.Orange
import com.emergetools.hackernews.ui.Screen
import com.emergetools.hackernews.ui.shared.LoadingIndicator
import com.emergetools.hackernews.ui.stories.StoriesViewModel
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

@Composable
fun StoryScreen(
  navController: NavController,
  id: Long,
) {
  val storiesViewModel: StoriesViewModel = mavericksActivityViewModel()
  val state by storiesViewModel.collectAsState()
  val story = state.stories[id]?.let {
    it as? Story
      ?: throw IllegalArgumentException("item $id not type Story, type: ${it.javaClass.simpleName}")
  } ?: throw IllegalArgumentException("item $id not found in stories map")

  Scaffold(
    topBar = {
      // TODO: Push up on scroll of webview
      TopAppBar(
        backgroundColor = Orange,
        title = {
          Text(
            text = story.title,
            color = Color.White,
            style = MaterialTheme.typography.body1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        },
        navigationIcon = {
          IconButton(onClick = navController::popBackStack) {
            Icon(
              imageVector = Icons.Filled.ArrowBack,
              contentDescription = stringResource(R.string.content_description_back),
            )
          }
        },
        actions = {
          IconButton(onClick = { navController.navigate(Screen.Comments.getRoute(id)) }) {
            Icon(
              imageVector = Icons.Default.Comment,
              contentDescription = stringResource(R.string.content_description_comment_button),
              tint = Color.White
            )
          }
        }
      )
    }
  ) {
    val webViewState = rememberWebViewState(url = story.url)

    if (webViewState.isLoading) {
      LoadingIndicator()
    }

    // TODO: Fix issue not showing for http (non-https) sites
    WebView(
      state = webViewState,
      onCreated = { webView ->
        webView.settings.javaScriptEnabled = true
      }
    )
  }
}
