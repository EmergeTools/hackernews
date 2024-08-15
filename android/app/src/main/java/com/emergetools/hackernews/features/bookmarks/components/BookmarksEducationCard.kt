package com.emergetools.hackernews.features.bookmarks.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.features.stories.StoryItem
import com.emergetools.hackernews.ui.components.StoryRow
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BookmarksEducationCard() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .background(MaterialTheme.colorScheme.background)
      .padding(vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(
      space = 8.dp,
      alignment = Alignment.CenterVertically
    ),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    val scale = remember { Animatable(0.8f) }
    var bookmarked by remember { mutableStateOf(false) }
    val nubRadius = remember { Animatable(30f) }
    val nubTranslation = remember { Animatable(0.1f) }
    val nubColor = MaterialTheme.colorScheme.onBackground.copy(0.2f)
    LaunchedEffect(Unit) {
      while (true) {
        delay(3000)
        nubTranslation.animateTo(0.3f)
        launch {
          scale.animateTo(0.7f)
        }
        launch {
          nubRadius.animateTo(20f)
        }
        delay(700)
        bookmarked = true
        delay(500)
        launch {
          scale.animateTo(0.8f)
        }
        launch {
          nubRadius.animateTo(30f)
        }
        delay(200)
        nubTranslation.animateTo(0.1f)
        delay(3000)
        nubTranslation.animateTo(0.3f)
        launch {
          scale.animateTo(0.7f)
        }
        launch {
          nubRadius.animateTo(20f)
        }
        delay(700)
        bookmarked = false
        delay(500)
        launch {
          scale.animateTo(0.8f)
        }
        launch {
          nubRadius.animateTo(30f)
        }
        delay(200)
        nubTranslation.animateTo(0.1f)
      }
    }
    Text(
      modifier = Modifier.wrapContentSize(),
      text = "Long press a story to bookmark it...",
      color = MaterialTheme.colorScheme.onBackground,
      style = MaterialTheme.typography.labelMedium,
      fontWeight = FontWeight.Medium
    )
    StoryRow(
      modifier = Modifier
        .drawWithContent {
          drawContent()

          val x = size.width * 0.8f
          val y = size.height * nubTranslation.value
          drawCircle(
            color = nubColor,
            center = Offset(x, y),
            radius = nubRadius.value
          )
          drawCircle(
            color = nubColor,
            center = Offset(x, y),
            radius = 30f
          )
        }
        .scale(scale.value)
        .border(
          width = 1.dp,
          color = MaterialTheme.colorScheme.onBackground,
        ),
      item = StoryItem.Content(
        id = 1L,
        title = "Show HN: A new Android client",
        author = "heyrikin",
        score = 10,
        commentCount = 45,
        epochTimestamp = 100L,
        timeLabel = "3h ago",
        bookmarked = bookmarked,
        url = ""
      ),
      onClick = {},
      onBookmark = {},
      onCommentClicked = {}
    )
    Text(
      modifier = Modifier.wrapContentSize(),
      text = "...and save it for later",
      color = MaterialTheme.colorScheme.onBackground,
      style = MaterialTheme.typography.labelMedium,
      fontWeight = FontWeight.Medium
    )
  }
}

@PreviewLightDark
@Composable
fun BookmarkEducationCardPreview() {
  HackerNewsTheme {
    BookmarksEducationCard()
  }
}
