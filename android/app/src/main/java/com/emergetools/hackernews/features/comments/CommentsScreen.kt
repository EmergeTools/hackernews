package com.emergetools.hackernews.features.comments

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.ui.theme.HNOrange
import com.emergetools.hackernews.ui.theme.HackerNewsTheme

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
      val lineColor = MaterialTheme.colorScheme.onBackground
      Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .height(16.dp)
        .drawBehind {
          val lineStart = Offset(0f, size.center.y)
          val lineEnd = Offset(size.width, size.center.y)
          drawLine(
            start = lineStart,
            end = lineEnd,
            color = lineColor,
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
      state = CommentsState.Content(
        title = "Show HN: A new HN client for Android",
        author = "rikinm",
        points = 69,
        text = null,
        comments = listOf(
          CommentState.Content(
            id = 1,
            level = 0,
            author = "rikinm",
            content = "Hello Child",
            timeLabel = "2d ago",
            children = listOf(
              CommentState.Content(
                id = 2,
                level = 1,
                author = "vasantm",
                content = "Hello Parent",
                timeLabel = "1h ago",
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
private fun CommentsScreenLoadingPreview() {
  HackerNewsTheme {
    CommentsScreen(
      state = CommentsState.Loading
    )
  }
}

@Preview
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
          children = listOf(
            CommentState.Content(
              id = 2,
              level = 1,
              author = "vasantm",
              content = "Hello Child",
              timeLabel = "2h ago",
              children = listOf()
            )
          )
        )
      )
    }
  }
}

@Preview
@Composable
fun CommentRowLoadingPreview() {
  HackerNewsTheme {
    Column {
      CommentRow(
        state = CommentState.Loading(level = 0)
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
    when (state) {
      is CommentState.Content -> {
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
            text = state.timeLabel,
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

      is CommentState.Loading -> {
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .width(40.dp)
              .height(14.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(HNOrange)
          )

          Box(
            modifier = Modifier
              .width(40.dp)
              .height(14.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(Color.Gray)
          )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(14.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(Color.LightGray)
          )
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(14.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(Color.LightGray)
          )
          Box(
            modifier = Modifier
              .fillMaxWidth(0.75f)
              .height(14.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(Color.LightGray)
          )
        }
      }
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
      state = HeaderState.Content(
        title = "Show HN: A super neat HN client for Android",
        author = "rikinm",
        points = 69,
        body = "Hi there"
      ),
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
    )
  }
}

@Preview
@Composable
private fun ItemHeaderLoadingPreview() {
  HackerNewsTheme {
    ItemHeader(
      state = HeaderState.Loading,
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
    when (state) {
      is HeaderState.Content -> {
        Text(
          text = state.title,
          style = MaterialTheme.typography.titleSmall
        )
        if (state.body != null) {
          Text(
            text = state.body.parseAsHtml(),
            style = MaterialTheme.typography.labelSmall,
          )
        }
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

      HeaderState.Loading -> {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(18.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(color = MaterialTheme.colorScheme.onBackground)
          )
          Box(
            modifier = Modifier
              .fillMaxWidth(0.75f)
              .height(18.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(color = MaterialTheme.colorScheme.onBackground)
          )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(14.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(color = Color.LightGray)
          )
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(14.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(color = Color.LightGray)
          )
          Box(
            modifier = Modifier
              .fillMaxWidth(0.75f)
              .height(14.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(color = Color.LightGray)
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(
            modifier = Modifier
              .width(30.dp)
              .height(14.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(Color.DarkGray)
          )
          Box(
            modifier = Modifier
              .width(30.dp)
              .height(14.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(MaterialTheme.colorScheme.onBackground)
          )
        }
      }
    }
  }
}