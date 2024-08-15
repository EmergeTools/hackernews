package com.emergetools.hackernews.features.stories.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emergetools.hackernews.features.stories.FeedType
import com.emergetools.hackernews.features.stories.NewsFeed
import com.emergetools.hackernews.features.stories.supportedFeeds
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import com.emergetools.hackernews.ui.theme.HackerOrange
import me.saket.extendedspans.ExtendedSpans
import me.saket.extendedspans.SquigglyUnderlineSpanPainter
import me.saket.extendedspans.drawBehind
import me.saket.extendedspans.rememberSquigglyUnderlineAnimator

@Composable
fun FeedHeader(
  modifier: Modifier = Modifier,
  feeds: List<NewsFeed>,
  onSelected: (FeedType) -> Unit
) {
  val scrollState = rememberScrollState()

  Row(
    modifier = modifier
      .horizontalScroll(scrollState)
      .fillMaxWidth()
      .wrapContentHeight()
      .background(color = MaterialTheme.colorScheme.background),
    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically
  ) {
    feeds.forEach { feed ->
      FeedHeaderItem(state = feed, select = onSelected)
    }
  }
}

@PreviewLightDark
@Composable
fun FeedHeaderPreview() {
  HackerNewsTheme {
    FeedHeader(
      feeds = supportedFeeds,
      onSelected = {}
    )
  }
}

@Composable
fun FeedHeaderItem(state: NewsFeed, select: (FeedType) -> Unit) {
  val squigglyUnderlineAnimator = rememberSquigglyUnderlineAnimator()
  val spans = remember(state.selected) {
    ExtendedSpans(
      SquigglyUnderlineSpanPainter(
        width = if (state.selected) 2.sp else 0.sp,
        amplitude = if (state.selected) 1.sp else 0.sp,
        animator = squigglyUnderlineAnimator
      )
    )
  }
  val label = remember(state) {
    AnnotatedString(
      text = state.type.label,
      spanStyle = if (state.selected) {
        SpanStyle(textDecoration = TextDecoration.Underline)
      } else {
        SpanStyle()
      }
    )
  }

  val scale by animateFloatAsState(
    targetValue = if (state.selected) 1.0f else 0.8f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow
    ),
    label = "Selection Scale"
  )
  Text(
    modifier = Modifier
      .scale(scale)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {
        select(state.type)
      }
      .padding(8.dp)
      .drawBehind(spans),
    text = spans.extend(label),
    color = if (state.selected) HackerOrange else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
    style = MaterialTheme.typography.titleMedium,
    onTextLayout = { result ->
      spans.onTextLayout(result)
    }
  )
}

