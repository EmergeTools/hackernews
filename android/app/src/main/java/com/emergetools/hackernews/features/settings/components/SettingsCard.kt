package com.emergetools.hackernews.features.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emergetools.hackernews.ui.theme.HackerBlue
import com.emergetools.hackernews.ui.theme.HackerNewsTheme

@Composable
fun SettingsCard(
  leadingIcon: (@Composable () -> Unit)? = null,
  trailingIcon: (@Composable () -> Unit)? = null,
  label: String,
  onClicked: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .clickable { onClicked() }
      .background(color = MaterialTheme.colorScheme.surface)
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    leadingIcon?.invoke()
    Text(
      text = label,
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurface,
      fontSize = 16.sp,
      fontWeight = FontWeight.Medium
    )
    if (trailingIcon != null) {
      Spacer(modifier = Modifier.weight(1f))
      trailingIcon.invoke()
    }
  }
}

@PreviewLightDark
@Composable
fun SettingsCardPreview() {
  HackerNewsTheme {
    SettingsCard(
      leadingIcon = {
        Icon(
          modifier = Modifier.size(12.dp),
          imageVector = Icons.Rounded.Settings,
          tint = HackerBlue,
          contentDescription = ""
        )
      },
      label = "Settings Card"
    ) { }
  }
}

@Composable
fun SettingsSectionLabel(section: String) {
  Text(
    modifier = Modifier.fillMaxWidth(),
    text = section,
    style = MaterialTheme.typography.labelSmall,
    fontWeight = FontWeight.Medium,
    color = MaterialTheme.colorScheme.onBackground,
    fontSize = 12.sp
  )
}

@PreviewLightDark
@Composable
fun SettingsSectionLabelPreview() {
  HackerNewsTheme {
    Box(
      Modifier
        .wrapContentSize()
        .background(color = MaterialTheme.colorScheme.surface)
    ) {
      SettingsSectionLabel("Section Title")
    }
  }
}
