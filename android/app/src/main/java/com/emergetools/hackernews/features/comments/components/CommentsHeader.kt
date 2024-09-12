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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emergetools.hackernews.R
import com.emergetools.hackernews.data.relativeTimeStamp
import com.emergetools.hackernews.features.comments.BodyState
import com.emergetools.hackernews.features.comments.HeaderState
import com.emergetools.hackernews.ui.components.MetadataButton
import com.emergetools.hackernews.ui.components.MetadataTag
import com.emergetools.hackernews.ui.theme.HackerGreen
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import com.emergetools.hackernews.ui.theme.HackerOrange
import com.emergetools.hackernews.ui.theme.HackerPurple
import com.emergetools.hackernews.ui.util.parseAsHtml
import java.time.Instant

@Composable
fun CommentsHeader(
  state: HeaderState,
  modifier: Modifier = Modifier,
  onLikeTapped: (HeaderState.Content) -> Unit,
  onToggleBody: (Boolean) -> Unit
) {
  Column(
    modifier = modifier.background(color = MaterialTheme.colorScheme.background),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    when (state) {
      is HeaderState.Content -> {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
          Text(
            text = state.title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            fontSize = 20.sp
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "@${state.author}",
                color = HackerOrange,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
              )
            }
            MetadataTag(label = relativeTimeStamp(state.epochSeconds)) {
              Icon(
                modifier = Modifier.size(12.dp),
                painter = painterResource(R.drawable.ic_time_outline),
                tint = HackerPurple,
                contentDescription = "Time Posted"
              )
            }
            Spacer(modifier = Modifier.weight(1f))
            MetadataButton(
              label = "${state.points}",
              contentColor = if (state.upvoted) {
                HackerGreen
              } else {
                MaterialTheme.colorScheme.onSurface
              },
              onClick = { onLikeTapped(state) }
            ) {
              Icon(
                modifier = Modifier.size(12.dp),
                painter = painterResource(R.drawable.ic_upvote),
                tint = HackerGreen,
                contentDescription = "Upvotes"
              )
            }
          }
        }
        if (state.body.text != null) {
          Column(
            Modifier
              .fillMaxWidth()
              .wrapContentHeight()
              .clip(RoundedCornerShape(8.dp))
              .clickable { onToggleBody(!state.body.collapsed) }
              .background(color = MaterialTheme.colorScheme.surface)
              .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              modifier = Modifier
                .graphicsLayer {
                  rotationZ = if (state.body.collapsed) 180f else 0f
                }
                .size(12.dp),
              painter = painterResource(R.drawable.ic_collapse),
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Expand or Collapse"
            )
            Text(
              text = state.body.text.parseAsHtml(),
              color = MaterialTheme.colorScheme.onBackground,
              style = MaterialTheme.typography.labelSmall,
              overflow = TextOverflow.Ellipsis,
              maxLines = if (state.body.collapsed) 4 else Int.MAX_VALUE
            )
          }
        }
      }

      HeaderState.Loading -> {
        val infiniteTransition = rememberInfiniteTransition("Skeleton Loader")
        val skeletonAlpha by infiniteTransition.animateFloat(
          initialValue = 0.2f,
          targetValue = 0.6f,
          animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
          ),
          label = "Skeleton Alpha"
        )
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = skeletonAlpha))
            )
            Box(
              modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(18.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = skeletonAlpha))
            )
          }
        }
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .width(60.dp)
              .height(12.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(HackerOrange.copy(alpha = skeletonAlpha))
          )
          Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(HackerPurple.copy(alpha = skeletonAlpha))
            )
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = skeletonAlpha))
            )
          }
          Spacer(modifier = Modifier.weight(1f))
          Row(
            modifier = Modifier
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
              .padding(vertical = 4.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
          ) {
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(HackerGreen.copy(alpha = skeletonAlpha))
            )
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = skeletonAlpha))
            )
          }
        }
      }
    }
  }
}

@PreviewLightDark
@Composable
private fun CommentsHeaderPreview() {
  HackerNewsTheme {
    Box(
      modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .padding(8.dp)
    ) {
      CommentsHeader(
        state = HeaderState.Content(
          id = 0L,
          title = "Show HN: A super neat HN client for Android",
          author = "rikinm",
          points = 69,
          epochSeconds = Instant.now().minusSeconds(60 * 60 * 2).epochSecond,
          body = BodyState("Wassup HN. I just built a sick new Hacker News Android client."),
          upvoted = false,
          upvoteUrl = "",
        ),
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight(),
        onLikeTapped = {},
        onToggleBody = {}
      )
    }
  }
}

@PreviewLightDark
@Composable
private fun CommentsHeaderLoadingPreview() {
  HackerNewsTheme {
    Box(
      modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .padding(8.dp)
    ) {
      CommentsHeader(
        state = HeaderState.Loading,
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight(),
        onLikeTapped = {},
        onToggleBody = {}
      )
    }
  }
}

