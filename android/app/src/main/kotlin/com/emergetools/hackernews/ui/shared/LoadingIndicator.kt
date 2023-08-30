package com.emergetools.hackernews.ui.shared

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.ui.HNTheme
import com.emergetools.snapshots.annotations.IgnoreEmergeSnapshot

@Composable
fun LoadingIndicator() {
  BoxWithConstraints(
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp)
  ) {
    CircularProgressIndicator(
      modifier = Modifier
        .align(Alignment.Center)
    )
  }
}

@Preview("Loading indicator")
@Composable
fun LoadingIndicatorPreview() {
  HNTheme {
    LoadingIndicator()
  }
}

// A sample ignored snapshot
@Preview("Loading indicator (ignored)")
@IgnoreEmergeSnapshot
@Composable
fun LoadingIndicatorPreviewIgnored() {
  HNTheme {
    LoadingIndicator()
  }
}