package com.emergetools.hackernews.features.settings

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
fun SettingsScreen() {
  Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
    Text("Settings", style = MaterialTheme.typography.titleMedium)
  }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
  HackerNewsTheme {
    SettingsScreen()
  }
}