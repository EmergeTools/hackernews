package com.github.takahirom.roborazzi.usage.examples

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.emergetools.hackernews.features.stories.StoryItem
import com.emergetools.hackernews.ui.components.StoryRow
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(RobolectricTestRunner::class)
class StoryRowComposeTest {
  @get:Rule
  val composeRule = createComposeRule()

  @Test
  fun roborazziTest() {
    composeRule.setContent {
      HackerNewsTheme {
        StoryRow(
          item = StoryItem.Loading(id = 1L),
          onClick = {},
          onBookmark = {},
          onCommentClicked = {},
        )
      }
    }

    composeRule
      .onRoot()
      .captureRoboImage()
  }
}
