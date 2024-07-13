package com.emergetools.hackernews.features.bookmarks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.ui.theme.HackerNewsTheme

@Composable
fun BookmarksScreen() {
  Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
    Text("Bookmarks", style = MaterialTheme.typography.titleMedium)
  }
}

@Preview
@Composable
private fun BookmarksScreenPreview() {
  HackerNewsTheme {
    BookmarksScreen()
  }
}