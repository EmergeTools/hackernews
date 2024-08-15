package com.emergetools.hackernews.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.R
import com.emergetools.hackernews.features.stories.StoryItem
import com.emergetools.hackernews.ui.theme.HackerBlue
import com.emergetools.hackernews.ui.theme.HackerGreen
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import com.emergetools.hackernews.ui.theme.HackerOrange
import com.emergetools.hackernews.ui.theme.HackerPurple

@PreviewLightDark
@Composable
private fun StoryRowPreview() {
  HackerNewsTheme {
    StoryRow(
      item = StoryItem.Content(
        id = 1L,
        title = "Hello There",
        author = "heyrikin",
        score = 10,
        commentCount = 0,
        epochTimestamp = 100L,
        timeLabel = "2h ago",
        bookmarked = true,
        url = ""
      ),
      onClick = {},
      onBookmark = {},
      onCommentClicked = {},
    )
  }
}

@PreviewLightDark
@Composable
private fun StoryRowLoadingPreview() {
  HackerNewsTheme {
    StoryRow(
      item = StoryItem.Loading(id = 1L),
      onClick = {},
      onBookmark = {},
      onCommentClicked = {},
    )
  }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoryRow(
  modifier: Modifier = Modifier,
  item: StoryItem,
  onClick: (StoryItem.Content) -> Unit,
  onBookmark: (StoryItem.Content) -> Unit,
  onCommentClicked: (StoryItem.Content) -> Unit,
) {
  when (item) {
    is StoryItem.Content -> {
      val bookmarkHeight by animateFloatAsState(
        targetValue = if (item.bookmarked) {
          80f
        } else {
          0f
        }, animationSpec = spring(
          dampingRatio = if (item.bookmarked) {
            Spring.DampingRatioMediumBouncy
          } else {
            Spring.DampingRatioNoBouncy
          }, stiffness = Spring.StiffnessLow
        ), label = "Bookmark Height"
      )
      Column(
        modifier = modifier
          .fillMaxWidth()
          .heightIn(min = 100.dp)
          .background(color = MaterialTheme.colorScheme.background)
          .drawWithContent {
            drawContent()
            val startX = size.width * 0.75f
            val startY = 0f
            val bookmarkWidth = 50f

            val path = Path().apply {
              moveTo(startX, startY)
              lineTo(startX, startY + bookmarkHeight)
              lineTo(startX + bookmarkWidth / 2f, startY + bookmarkHeight * 0.75f)
              lineTo(startX + bookmarkWidth, startY + bookmarkHeight)
              lineTo(startX + bookmarkWidth, startY)
            }

            drawPath(
              path,
              color = HackerOrange,
            )
          }
          .combinedClickable(onClick = {
            onClick(item)
          }, onLongClick = {
            onBookmark(item)
          })
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(
          space = 8.dp,
          alignment = Alignment.CenterVertically
        )
      ) {
        Text(
          text = "@${item.author}",
          color = HackerOrange,
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = item.title,
          color = MaterialTheme.colorScheme.onSurface,
          style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(0.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          MetadataTag(
            label = "${item.score}",
          ) {
            Icon(
              modifier = Modifier.size(12.dp),
              painter = painterResource(R.drawable.ic_upvote),
              tint = HackerGreen,
              contentDescription = "Likes"
            )
          }
          MetadataTag(
            label = item.timeLabel,
          ) {
            Icon(
              modifier = Modifier.size(12.dp),
              painter = painterResource(R.drawable.ic_time_outline),
              tint = HackerPurple,
              contentDescription = "Time Posted"
            )
          }
          Spacer(modifier = Modifier.weight(1f))
          MetadataButton(
            label = "${item.commentCount}",
            onClick = { onCommentClicked(item) }
          ) {
            Icon(
              modifier = Modifier.size(12.dp),
              painter = painterResource(R.drawable.ic_chat),
              tint = HackerBlue,
              contentDescription = "Comments"
            )
          }
        }
      }
    }

    is StoryItem.Loading -> {
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
          .heightIn(min = 100.dp)
          .background(color = MaterialTheme.colorScheme.background)
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(0.15f)
            .height(12.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color = HackerOrange.copy(alpha = skeletonAlpha))
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Box(
            modifier = Modifier
              .fillMaxWidth(0.75f)
              .height(12.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
          )
          Box(
            modifier = Modifier
              .fillMaxWidth(0.5f)
              .height(12.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
          )
        }
        Spacer(modifier = Modifier.height(0.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
          ) {
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = HackerGreen.copy(alpha = skeletonAlpha))
            )
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
            )
          }
          Row(
            modifier = Modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
          ) {
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = HackerPurple.copy(alpha = skeletonAlpha))
            )
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
            )
          }
          Spacer(modifier = Modifier.weight(1f))
          Row(
            modifier = Modifier
              .wrapContentSize()
              .clip(CircleShape)
              .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
              .padding(vertical = 4.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
          ) {
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = HackerBlue.copy(alpha = skeletonAlpha))
            )
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
            )
          }
        }
      }
    }
  }
}
