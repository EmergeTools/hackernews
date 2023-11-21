package com.emergetools.hackernews

import android.os.Bundle
import android.os.Trace
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import com.emergetools.hackernews.ui.HNNavHost
import com.emergetools.hackernews.ui.HNTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

  @OptIn(ExperimentalAnimationApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    Trace.beginSection("MainActivity.onCreate")
    super.onCreate(savedInstanceState)

    runBlocking {
      delay(100)
    }

    setContent {
      HNTheme {
        HNNavHost()
      }
    }

    Trace.endSection()
  }
}
