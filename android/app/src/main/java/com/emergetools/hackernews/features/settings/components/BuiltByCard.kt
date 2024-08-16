package com.emergetools.hackernews.features.settings.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import com.emergetools.hackernews.ui.theme.DeepSpaceBlue
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

@Preview
@Composable
fun BuiltByCardPreview() {
  HackerNewsTheme {
    BuiltByCard()
  }
}

