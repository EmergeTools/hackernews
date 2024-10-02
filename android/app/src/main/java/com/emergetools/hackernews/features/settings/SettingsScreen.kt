package com.emergetools.hackernews.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.R
import com.emergetools.hackernews.features.settings.components.BuiltByCard
import com.emergetools.hackernews.features.settings.components.LoginCard
import com.emergetools.hackernews.features.settings.components.SettingsCard
import com.emergetools.hackernews.features.settings.components.SettingsSectionLabel
import com.emergetools.hackernews.ui.preview.AppStoreSnapshot
import com.emergetools.hackernews.ui.preview.SnapshotPreview
import com.emergetools.hackernews.ui.theme.HackerBlue
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import com.emergetools.hackernews.ui.theme.HackerOrange
import com.emergetools.hackernews.ui.theme.HackerRed
import com.emergetools.snapshots.annotations.EmergeAppStoreSnapshot

@Composable
fun SettingsScreen(
  state: SettingsState,
  actions: (SettingsAction) -> Unit,
  navigation: (SettingsNavigation) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(color = MaterialTheme.colorScheme.background),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "Settings",
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onBackground
    )
    Column(
      modifier = Modifier
        .verticalScroll(state = rememberScrollState())
        .padding(horizontal = 8.dp),
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
            contentDescription = "Send Feedback"
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
      ) {
        navigation(SettingsNavigation.GoToSettingsLink("https://forms.gle/YYno9sUehE5xuKAq9"))
      }
      SettingsCard(
        leadingIcon = {
          Icon(
            modifier = Modifier.width(14.dp),
            imageVector = Icons.Rounded.Lock,
            tint = HackerRed,
            contentDescription = "Privacy Policy"
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
        label = "Privacy Policy"
      ) {
        navigation(SettingsNavigation.GoToSettingsLink("https://www.emergetools.com/HackerNewsPrivacyPolicy.html"))
      }
    }
  }
}

@OptIn(EmergeAppStoreSnapshot::class)
@SnapshotPreview
@AppStoreSnapshot
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

@Preview(
  device = Devices.PIXEL_2,
  showSystemUi = true
)
@Composable
private fun SettingsSmallScreenPreview() {
  HackerNewsTheme {
    Scaffold(
      bottomBar = {
        SettingsPreviewNavBar()
      }
    ) { innerPadding ->
      Box(modifier = Modifier.padding(innerPadding)) {
        SettingsScreen(
          state = SettingsState(false),
          actions = {},
          navigation = {}
        )
      }
    }
  }
}

@Composable
private fun SettingsPreviewNavBar() {
  NavigationBar {
    NavigationBarItem(
      selected = false,
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
      ),
      onClick = { },
      icon = {
        Icon(
          painter = painterResource(R.drawable.ic_feed),
          contentDescription = "feed"
        )
      },
    )
    NavigationBarItem(
      selected = false,
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
      ),
      onClick = { },
      icon = {
        Icon(
          painter = painterResource(R.drawable.ic_bookmarks),
          contentDescription = "bookmarks"
        )
      },
    )
    NavigationBarItem(
      selected = true,
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
      ),
      onClick = { },
      icon = {
        Icon(
          painter = painterResource(R.drawable.ic_settings),
          contentDescription = "settings"
        )
      },
    )
  }
}
