package com.emergetools.hackernews.features.stories.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import com.emergetools.hackernews.ui.theme.HackerRed

@Composable
fun FeedErrorCard(modifier: Modifier = Modifier, onRefresh: () -> Unit) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .height(200.dp)
      .background(color = MaterialTheme.colorScheme.background),
    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Icon(
      imageVector = Icons.Rounded.Warning,
      tint = HackerRed,
      contentDescription = "Failed to Load",
    )

    Text(
      text = "Failed to Load Feed",
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.onBackground
    )

    Button(
      colors = ButtonDefaults.buttonColors(
        containerColor = HackerRed,
        contentColor = Color.White
      ),
      onClick = { onRefresh() }) {
      Icon(
        imageVector = Icons.Rounded.Refresh,
        contentDescription = "Reload Feed"
      )
    }
  }
}

@PreviewLightDark
@Composable
fun FeedErrorCardPreview() {
  HackerNewsTheme {
    FeedErrorCard(
      onRefresh = {}
    )
  }
}

