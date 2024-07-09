package com.emergetools.hackernews.features.stories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.emergetools.hackernews.ui.theme.HackerNewsTheme

@Composable
fun StoryScreen(url: String) {
  val webViewState = rememberWebViewState(url)
  Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = url,
      style = MaterialTheme.typography.labelSmall,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
    WebView(
      modifier = Modifier.fillMaxWidth().weight(1f),
      state = webViewState
    )
  }
}

@Preview
@Composable
private fun StoryScreenPreview() {
  HackerNewsTheme {
    StoryScreen("www.google.com")
  }
}