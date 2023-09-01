package com.emergetools.hackernews

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import com.emergetools.hackernews.ui.HNNavHost
import com.emergetools.hackernews.ui.HNTheme

class MainActivity : AppCompatActivity() {

  @OptIn(ExperimentalAnimationApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      HNTheme {
        HNNavHost()
      }
    }
  }
}
