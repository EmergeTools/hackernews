package com.emergetools.hackernews.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emergetools.hackernews.ui.theme.HackerGreen
import com.emergetools.hackernews.ui.theme.HackerNewsTheme

@Composable
fun SettingsScreen(
  state: SettingsState,
  actions: (SettingsAction) -> Unit,
  navigation: (SettingsNavigation) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(color = MaterialTheme.colorScheme.background)
      .padding(8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "Settings",
      modifier = Modifier.fillMaxWidth(),
      style = MaterialTheme.typography.titleMedium
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(
          color = MaterialTheme.colorScheme.surfaceContainer
        )
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape)
          .background(
            color = if (state.loggedIn) {
              HackerGreen
            } else {
              MaterialTheme.colorScheme.surfaceDim
            }
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = if (!state.loggedIn) {
            "🤔"
          } else {
            "😎"
          },
          fontSize = 24.sp
        )
      }

      Button(
        onClick = {
          navigation(SettingsNavigation.GoToLogin)
        }
      ) {
        Text(
          text = if (!state.loggedIn) {
            "Login"
          } else {
            "Logout"
          },
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
  HackerNewsTheme {
    SettingsScreen(
      state = SettingsState(false),
      actions = {},
      navigation = {}
    )
  }
}