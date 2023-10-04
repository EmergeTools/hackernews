package com.emergetools.hackernews.ui.annotations

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
  name = "Light mode",
  uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
  name = "Dark mode",
  uiMode = Configuration.UI_MODE_NIGHT_YES,
)
annotation class LightDarkPreviews
