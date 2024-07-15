package com.emergetools.hackernews.data

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val sharedIntent = CustomTabsIntent.Builder().build()
val LocalCustomTabsIntent = staticCompositionLocalOf<CustomTabsIntent> {
  error("LocalCustomTabsIntent not provided")
}

@Composable
fun ChromeTabsProvider(
  content: @Composable () -> Unit
) {
  CompositionLocalProvider(
    LocalCustomTabsIntent provides sharedIntent
  ) {
    content()
  }
}

