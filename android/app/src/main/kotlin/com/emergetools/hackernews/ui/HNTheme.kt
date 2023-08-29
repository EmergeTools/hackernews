package com.emergetools.hackernews.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Orange = Color(0xffff6600)
private val Purple = Color(0xff221E43)

// TODO: Success
private val Error = Color(0xffe53935)

const val ALPHA_SECONDARY = .56f
const val ALPHA_TERTIARY = .27f

object Dark {
  val Background = Color(0xff1D1E21)
  val Card = Color(0xff2B2C2F)
  val Divider = Color(0xff4c5053)
  val TextPrimary = Color(0xffffffff)
  val TextSecondary = Color(0xffC4C4C4)
  val TextTertiary = Color(0xff878787)
}

object Light {
  val Background = Color(0xfff6f6ef)
  val Card = Color(0xffffffff)
  val Divider = Color(0xffdddddd)
  val TextPrimary = Color(0xff222222)
  val TextSecondary = Color(0xff717171)
  val TextTertiary = Color(0xffb0b0b0)
}

// TODO: Add textSecondary/tertiary
private val DarkColors = darkColors(
  primary = Orange,
  secondary = Purple,
  background = Dark.Background,
  surface = Dark.Card,
  error = Error,
  onPrimary = Dark.TextPrimary,
  onSecondary = Dark.TextPrimary,
  onBackground = Dark.TextPrimary,
  onSurface = Dark.TextPrimary,
)
private val LightColors = darkColors(
  primary = Orange,
  secondary = Purple,
  background = Light.Background,
  surface = Light.Card,
  error = Error,
  onPrimary = Dark.TextPrimary,
  onSecondary = Dark.TextPrimary,
  onBackground = Light.TextPrimary,
  onSurface = Light.TextPrimary,
)

@Composable
fun HNTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = MaterialTheme(
  colors = if (darkTheme) DarkColors else LightColors,
  content = content
)