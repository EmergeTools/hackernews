package com.emergetools.hackernews.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.R
import com.emergetools.hackernews.ui.theme.HackerBlue
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import com.emergetools.hackernews.ui.theme.HackerOrange

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
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onBackground
    )
    Column(
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      SettingsSectionLabel("Profile")
      LoginCard(
        state = state,
        onClicked = {
          if (state.loggedIn) {
            actions(SettingsAction.LogoutPressed)
          } else {
            navigation(SettingsNavigation.GoToLogin)
          }
        }
      )
      Spacer(modifier = Modifier.height(8.dp))
      SettingsSectionLabel("About")
      BuiltByCard {
        navigation(SettingsNavigation.GoToSettingsLink("https://www.emergetools.com"))
      }
      SettingsCard(
        leadingIcon = {
          Icon(
            modifier = Modifier.width(12.dp),
            painter = painterResource(R.drawable.ic_twitter),
            tint = HackerBlue,
            contentDescription = "Twitter"
          )
        },
        trailingIcon = {
          Icon(
            modifier = Modifier.width(12.dp),
            painter = painterResource(R.drawable.ic_arrow_up_right),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = "Link"
          )
        },
        label = "Follow Emerge"
      ) {
        navigation(SettingsNavigation.GoToSettingsLink("https://www.twitter.com/emergetools"))
      }
      SettingsCard(
        leadingIcon = {
          Icon(
            modifier = Modifier.width(12.dp),
            painter = painterResource(R.drawable.ic_twitter),
            tint = HackerBlue,
            contentDescription = "Twitter"
          )
        },
        trailingIcon = {
          Icon(
            modifier = Modifier.width(12.dp),
            painter = painterResource(R.drawable.ic_arrow_up_right),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = "Link"
          )
        },
        label = "Follow Supergooey"
      ) {
        navigation(SettingsNavigation.GoToSettingsLink("https://www.twitter.com/heyrikin"))
      }
      SettingsCard(
        leadingIcon = {
          Icon(
            modifier = Modifier.width(14.dp),
            imageVector = Icons.Rounded.Warning,
            tint = HackerOrange,
            contentDescription = "Twitter"
          )
        },
        trailingIcon = {
          Icon(
            modifier = Modifier.width(12.dp),
            painter = painterResource(R.drawable.ic_arrow_up_right),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = "Link"
          )
        },
        label = "Send Feedback"
      ) { }
    }
  }
}

@PreviewLightDark
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
