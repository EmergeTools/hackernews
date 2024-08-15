package com.emergetools.hackernews.features.comments.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.R
import com.emergetools.hackernews.features.comments.CommentState
import com.emergetools.hackernews.features.comments.HiddenStatus
import com.emergetools.hackernews.ui.components.MetadataTag
import com.emergetools.hackernews.ui.theme.HackerGreen
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import com.emergetools.hackernews.ui.util.parseAsHtml

@Composable
fun CommentRow(
  modifier: Modifier = Modifier,
  state: CommentState,
  onToggleHide: (CommentState.Content) -> Unit,
  onLikeTapped: (CommentState.Content) -> Unit
) {
  when (state) {
    is CommentState.Content -> {
      val startPadding = (state.level * 16).dp
      Column(
        modifier = modifier
          .padding(start = startPadding)
          .fillMaxWidth()
          .wrapContentHeight()
          .clip(RoundedCornerShape(8.dp))
          .clickable { onToggleHide(state) }
          .background(color = MaterialTheme.colorScheme.surface)
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "@${state.author}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
          )
          MetadataTag(
            label = state.timeLabel
          ) {
            Icon(
              modifier = Modifier.size(12.dp),
              painter = painterResource(R.drawable.ic_time_outline),
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Time Posted"
            )
          }
          Icon(
            modifier = Modifier
              .graphicsLayer {
                rotationZ = if (state.hidden == HiddenStatus.Hidden) 180f else 0f
              }
              .size(12.dp),
            painter = painterResource(R.drawable.ic_collapse),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = "Expand or Collapse"
          )
          Spacer(modifier = Modifier.weight(1f))
          Box(
            modifier = Modifier
              .wrapContentSize()
              .clip(CircleShape)
              .clickable { onLikeTapped(state) }
              .background(
                color = if (state.upvoted) {
                  HackerGreen.copy(alpha = 0.2f)
                } else {
                  MaterialTheme.colorScheme.surfaceContainerHighest
                }
              )
              .padding(vertical = 4.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              modifier = Modifier.size(12.dp),
              painter = painterResource(R.drawable.ic_upvote),
              tint = if (state.upvoted) {
                HackerGreen
              } else {
                MaterialTheme.colorScheme.onSurface
              },
              contentDescription = "Upvote"
            )
          }
        }
        if (state.hidden == HiddenStatus.Displayed) {
          Text(
            text = state.content.parseAsHtml(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
      }
    }

    is CommentState.Loading -> {
      val infiniteTransition = rememberInfiniteTransition("Skeleton")
      val skeletonAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
          animation = tween(durationMillis = 1000, easing = LinearEasing),
          repeatMode = RepeatMode.Reverse,
        ),
        label = "Skeleton Alpha"
      )
      Column(
        modifier = modifier
          .fillMaxWidth()
          .wrapContentHeight()
          .clip(RoundedCornerShape(8.dp))
          .background(color = MaterialTheme.colorScheme.surface)
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .width(40.dp)
              .height(12.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
          )

          Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
            )
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
            )
          }
          Spacer(modifier = Modifier.weight(1f))
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
              .padding(vertical = 4.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
            )
          }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(12.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
          )
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(12.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
          )
          Box(
            modifier = Modifier
              .fillMaxWidth(0.75f)
              .height(12.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
          )
        }
      }
    }
  }
}

@PreviewLightDark
@Composable
fun CommentRowPreview() {
  HackerNewsTheme {
    Column {
      CommentRow(
        state = CommentState.Content(
          id = 1,
          level = 0,
          author = "rikinm",
          content = "Hello Parent",
          timeLabel = "2d ago",
          upvoted = false,
          upvoteUrl = "",
          children = listOf()
        ),
        onToggleHide = {},
        onLikeTapped = {}
      )
    }
  }
}

@PreviewLightDark
@Composable
fun CommentRowLoadingPreview() {
  HackerNewsTheme {
    Column {
      CommentRow(
        state = CommentState.Loading(level = 0),
        onToggleHide = {},
        onLikeTapped = {}
      )
    }
  }
}

