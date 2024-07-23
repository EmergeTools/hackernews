package com.emergetools.hackernews.features.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradient
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emergetools.hackernews.R
import com.emergetools.hackernews.ui.theme.HackerBlue
import com.emergetools.hackernews.ui.theme.HackerGreen
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import com.emergetools.hackernews.ui.theme.HackerRed

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
      Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Profile",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onBackground,
          fontSize = 12.sp
        )
      }
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

@Composable
fun LoginCard(
  state: SettingsState,
  onClicked: () -> Unit
) {
  val iconScale by animateFloatAsState(
    targetValue = if (state.loggedIn) 1.5f else 1f,
    label = "Icon Scale"
  )
  val personColor by animateColorAsState(
    targetValue = if (state.loggedIn) HackerRed else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    label = "Person Icon Color"
  )
  val likeColor by animateColorAsState(
    targetValue = if (state.loggedIn) HackerGreen else MaterialTheme.colorScheme.onSurface.copy(
      alpha = 0.2f
    ),
    label = "Like Icon Color"
  )
  val likeRotation by animateFloatAsState(
    targetValue = if (state.loggedIn) 5f else 0f,
    animationSpec = spring(
      stiffness = Spring.StiffnessVeryLow,
      dampingRatio = Spring.DampingRatioHighBouncy
    ),
    label = "Like Icon Rotation"
  )
  val commentColor by animateColorAsState(
    targetValue = if (state.loggedIn) HackerBlue else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    label = "Comment Icon Color"
  )
  val commentRotation by animateFloatAsState(
    targetValue = if (state.loggedIn) -5f else 0f,
    animationSpec = spring(
      stiffness = Spring.StiffnessVeryLow,
      dampingRatio = Spring.DampingRatioHighBouncy
    ),
    label = "Comment Icon Rotation"
  )
  val glowColor by animateColorAsState(
    targetValue = if (state.loggedIn) HackerGreen else Color.Transparent,
    animationSpec = tween(durationMillis = 600, easing = LinearEasing),
    label = "Glow Color"
  )
  val blinkColor by animateColorAsState(
    targetValue = if (state.loggedIn) {
      HackerGreen
    } else {
      MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
    },
    label = "Glow Color"
  )
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(color = MaterialTheme.colorScheme.surface)
      .padding(16.dp)
      .clickable { onClicked() }
  ) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Box(
          modifier = Modifier
            .size(16.dp)
            .drawBehind {
              val blinkRadius = size.width * 0.15f
              val glowRadius = size.width
              drawCircle(
                radius = glowRadius,
                brush = Brush.radialGradient(
                  0.0f to glowColor,
                  1.0f to Color.Transparent
                )
              )
              drawCircle(
                radius = blinkRadius,
                color = blinkColor
              )
            },
        )
        Text(
          text = if (state.loggedIn) "Logout" else "Login",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }
      Spacer(modifier = Modifier.weight(1f))
      Row(
        horizontalArrangement = Arrangement.spacedBy(
          space = 8.dp,
          alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          modifier = Modifier
            .graphicsLayer {
              rotationZ = likeRotation
              scaleX = iconScale
              scaleY = iconScale
            }
            .size(12.dp),
          imageVector = Icons.Rounded.ThumbUp,
          tint = likeColor,
          contentDescription = "Likes"
        )
        Icon(
          modifier = Modifier
            .graphicsLayer {
              rotationZ = commentRotation
              scaleX = iconScale
              scaleY = iconScale
            }
            .size(12.dp),
          painter = painterResource(R.drawable.ic_chat),
          tint = commentColor,
          contentDescription = "Comments"
        )
      }
    }
  }
}

@PreviewLightDark
@Composable
private fun LoginCardPreview() {
  var loggedIn by remember { mutableStateOf(false) }
  HackerNewsTheme {
    LoginCard(
      state = SettingsState(loggedIn = loggedIn),
      onClicked = { loggedIn = !loggedIn}
    )
  }
}

@PreviewLightDark
@Composable
private fun LoginCardLoggedInPreview() {
  HackerNewsTheme {
    LoginCard(
      state = SettingsState(loggedIn = true),
      onClicked = {}
    )
  }
}
