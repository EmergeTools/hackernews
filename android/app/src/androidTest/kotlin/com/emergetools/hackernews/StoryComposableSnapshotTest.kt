package com.emergetools.hackernews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.emergetools.hackernews.network.models.Story
import com.emergetools.hackernews.ui.items.BuildStory

/**
 * Example generated snapshot test from androidTest source set.
 * To generate a snapshot test for this preview, add the androidTest source set to the debug variant.
 */
@Preview
@Composable
fun StoryRow() {
  val mockStory = Story(
    id = 1,
    by = "Ryan B",
    time = 0,
    title = "Mock Story title for snapshot",
    text = "This is a mock story I wrote for the test",
    url = "https://www.example.com",
    score = 100,
    descendants = 10,
    comments = emptyList()
  )
  BuildStory(
    story = mockStory,
    onItemClick = {},
    onItemButtonClick = {}
  )
}
