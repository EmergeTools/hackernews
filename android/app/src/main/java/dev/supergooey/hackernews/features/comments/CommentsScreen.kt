package dev.supergooey.hackernews.features.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.supergooey.hackernews.ui.theme.HNOrange
import dev.supergooey.hackernews.ui.theme.HNOrangeLight
import dev.supergooey.hackernews.ui.theme.HackerNewsTheme

@Composable
fun CommentsScreen(state: CommentsState) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(color = MaterialTheme.colorScheme.background),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    item {
      ItemHeader(
        state = state.headerState,
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
      )
    }
    item {
      Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .height(16.dp)
        .drawBehind {
          val lineStart = Offset(0f, size.center.y)
          val lineEnd = Offset(size.width, size.center.y)
          drawLine(
            start = lineStart,
            end = lineEnd,
            color = HNOrange,
            strokeWidth = 4f,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(
              intervals = floatArrayOf(20f, 20f)
            )
          )
        }
      )
    }
    items(items = state.comments) { comment ->
      CommentRow(comment)
    }
  }
}

@Preview
@Composable
private fun CommentsScreenPreview() {
  HackerNewsTheme {
    CommentsScreen(
      state = CommentsState(
        title = "Show HN: A new HN client for Android",
        author = "rikinm",
        points = 69,
        comments = listOf(
          CommentState(
            id = 1,
            level = 0,
            author = "rikinm",
            content = "Hello Child",
            children = listOf(
              CommentState(
                id = 2,
                level = 1,
                author = "vasantm",
                content = "Hello Parent",
                children = listOf()
              )
            )
          )
        )
      )
    )
  }
}

@Preview
@Composable
private fun CommentRowPreview() {
  HackerNewsTheme {
    Column {
      CommentRow(
        state = CommentState(
          id = 1,
          level = 0,
          author = "rikinm",
          content = "Hello Parent",
          children = listOf(
            CommentState(
              id = 2,
              level = 1,
              author = "vasantm",
              content = "Hello Child",
              children = listOf()
            )
          )
        )
      )
    }
  }
}

@Composable
fun CommentRow(state: CommentState) {
  val startPadding = (state.level * 16).dp
  Column(
    modifier = Modifier
      .padding(start = startPadding)
      .fillMaxWidth()
      .heightIn(min = 80.dp)
      .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
      .background(color = MaterialTheme.colorScheme.surfaceContainer)
      .padding(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = state.author,
        style = MaterialTheme.typography.labelSmall,
        color = HNOrange,
        fontWeight = FontWeight.Medium
      )
      Text(
        "•",
        style = MaterialTheme.typography.labelSmall
      )
      Text(
        "1h ago",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Medium,
        color = Color.Gray
      )
      Spacer(modifier = Modifier.weight(1f))
      Icon(
        modifier = Modifier.size(16.dp),
        imageVector = Icons.Default.ThumbUp,
        contentDescription = "upvote"
      )
      Icon(
        modifier = Modifier.size(16.dp),
        imageVector = Icons.Default.MoreVert,
        contentDescription = "options"
      )
    }
    Row {
      Text(
        text = state.content.parseAsHtml(),
        style = MaterialTheme.typography.labelSmall
      )
    }
  }
  state.children.forEach { child ->
    CommentRow(child)
  }
}

@Preview
@Composable
private fun ItemHeaderPreview() {
  HackerNewsTheme {
    ItemHeader(
      state = HeaderState(
        title = "Show HN: A super neat HN client for Android",
        author = "rikinm",
        points = 69
      ),
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
    )
  }
}

@Composable
fun ItemHeader(
  state: HeaderState,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .background(color = MaterialTheme.colorScheme.background)
      .padding(8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = state.title,
      style = MaterialTheme.typography.titleSmall
    )
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "${state.points}",
        style = MaterialTheme.typography.labelSmall
      )
      Text(
        text = "•",
        style = MaterialTheme.typography.labelSmall
      )
      Text(
        text = state.author,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Medium
      )
    }
  }
}