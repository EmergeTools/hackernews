package com.emergetools.hackernews.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
  primary = HackerBlue,
  primaryContainer = HackerOrangeLight,
  background = BackgroundLight,
  surface = SurfaceLight,
  surfaceContainer = SurfaceLight,
  onBackground = OnBackgroundLight,
  onSurface = OnSurfaceLight
)

private val DarkColorScheme = darkColorScheme(
  primary = HackerBlue,
  primaryContainer = HackerOrangeLight,
  background = BackgroundDark,
  surface = SurfaceDark,
  surfaceContainer = SurfaceDark,
  onBackground = OnBackgroundDark,
  onSurface = OnSurfaceDark
)


@Composable
fun HackerNewsTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }

    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
