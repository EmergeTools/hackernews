package com.emergetools.hackernews.features.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import com.emergetools.hackernews.R
import com.emergetools.hackernews.ui.theme.DeepSpaceBlue
import com.emergetools.hackernews.ui.theme.HackerBlue
import com.emergetools.hackernews.ui.theme.HackerGreen
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import com.emergetools.hackernews.ui.theme.MidnightBlue
import com.emergetools.hackernews.ui.theme.unbounded
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

private fun Int.pi() = this * PI.toFloat()
private fun fract(x: Float) = x - floor(x)
private fun dot(one: Offset, two: Offset): Float {
  return (one.x * two.x) + (one.y * two.y)
}

private fun rand(vec2: Offset): Float {
  return fract(sin(dot(vec2, Offset(12.9898f, 78.233f))) * 43758.547f)
}

@Composable
private fun SpaceBackground(
  modifier: Modifier = Modifier
) {
  var time by remember { mutableFloatStateOf(0f) }
  LaunchedEffect(Unit) {
    do {
      withFrameMillis {
        time += 0.01f
      }
    } while (true)
  }

  Canvas(
    modifier = modifier.background(
      brush = Brush.radialGradient(
        0.1f to DeepSpaceBlue,
        1.0f to MidnightBlue,
        radius = 700f
      )
    )
  ) {
    val layers = 10
    val starsPerLayer = 30

    for (layer in 1..layers) {
      var scale = fract((time / 4f) + (layer / layers.toFloat())) // 0f..1f
      val alpha = 1f - (cos(scale * 2.pi()) + 1) / 2f // 0..1..0 fade in then fade out
      scale += 0.6f // shifts the range to 0.6f..1.6f

      for (star in 1..starsPerLayer) {
        // pick a random position to draw a circle
        val randomXFactor = 6.059f
        val randomYFactor = 4.321f
        val x =
          size.width * ((sin(star.toFloat() * layer.toFloat() * randomXFactor) * scale) + 1) / 2f
        val y =
          size.height * ((sin(star.toFloat() * layer.toFloat() * randomYFactor) * scale) + 1) / 2f
        val r = abs(cos(star.toFloat())) * 16.0f * scale

        val vertices = (star * layer) % 5 + 3
        val shape = if (vertices % 2 == 0) {
          RoundedPolygon.star(
            numVerticesPerRadius = vertices,
            innerRadius = r * 0.2f,
            radius = r,
            rounding = CornerRounding(r * 0.03f),
            centerX = x,
            centerY = y
          )
        } else {
          RoundedPolygon(
            numVertices = vertices,
            radius = r,
            rounding = CornerRounding(r * 0.2f),
            centerX = x,
            centerY = y
          )
        }
        withTransform({
          rotate(
            degrees = (time / 2f) * rand(Offset(layer.toFloat(), star.toFloat())) * 360f,
            pivot = Offset(x, y)
          )
        }) {
          drawPath(
            path = shape.toPath().asComposePath(),
            color = Color(1f, 1f, 1f, alpha)
          )
        }
      }
    }
  }
}

@Composable
fun BuiltByCard(
  onClick: () -> Unit = {}
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(240.dp)
      .clip(RoundedCornerShape(8.dp))
      .clickable { onClick() },
    contentAlignment = Alignment.Center
  ) {
    SpaceBackground(modifier = Modifier.fillMaxSize())
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "Built with 👽 by",
        color = Color.White,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold
      )
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = "emerge",
          color = Color.White,
          style = MaterialTheme.typography.titleSmall,
          fontSize = 18.sp
        )
        Text("✚", color = Color.White, fontSize = 12.sp)
        Text(
          text = "supergooey",
          color = Color.White,
          fontFamily = unbounded,
          style = MaterialTheme.typography.titleSmall
        )
      }
    }
  }
}

//@Preview
@Composable
fun BuiltByCardPrevew() {
  HackerNewsTheme {
    BuiltByCard()
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
      .clickable { onClicked() }
      .background(color = MaterialTheme.colorScheme.surface)
      .padding(16.dp)
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
          painter = painterResource(R.drawable.ic_upvote),
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
      onClicked = { loggedIn = !loggedIn }
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
          imageVector = Icons.Rounded.Home,
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
