package com.emergetools.hackernews.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.R
import com.emergetools.hackernews.ui.theme.HackerBlue
import com.emergetools.hackernews.ui.theme.HackerGreen
import com.emergetools.hackernews.ui.theme.HackerNewsTheme

@Composable
fun MetadataTag(
  label: String,
  contentColor: Color = MaterialTheme.colorScheme.onSurface,
  icon: @Composable () -> Unit
) {
  Row(
    modifier = Modifier.wrapContentSize(),
    horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically
  ) {
    icon()
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.Medium,
      color = contentColor
    )
  }
}

@PreviewLightDark
@Composable
fun MetadataTagPreview() {
  HackerNewsTheme {
    Surface {
      MetadataTag(
        label = "100"
      ) {
        Icon(
          modifier = Modifier.size(12.dp),
          painter = painterResource(R.drawable.ic_upvote),
          tint = HackerGreen,
          contentDescription = "Upvote"
        )
      }
    }
  }
}

@Composable
fun MetadataButton(
  label: String,
  contentColor: Color = MaterialTheme.colorScheme.onSurface,
  backgroundColor: Color = contentColor.copy(alpha = 0.1f),
  onClick: () -> Unit = {},
  icon: @Composable () -> Unit,
) {
  Row(
    modifier = Modifier
      .wrapContentSize()
      .clip(CircleShape)
      .clickable { onClick() }
      .background(color = backgroundColor)
      .padding(vertical = 4.dp, horizontal = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically
  ) {
    icon()
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.Medium,
      color = contentColor
    )
  }
}

@PreviewLightDark
@Composable
fun MetadataButtonPreview() {
  HackerNewsTheme {
    Surface {
      MetadataButton(
        label = "25"
      ) {
        Icon(
          modifier = Modifier.size(12.dp),
          painter = painterResource(R.drawable.ic_chat),
          tint = HackerBlue,
          contentDescription = "Comment"
        )
      }
    }
  }
}


