package com.emergetools.hackernews.features.comments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.R
import com.emergetools.hackernews.features.stories.MetadataTag
import com.emergetools.hackernews.ui.theme.HackerBlue
import com.emergetools.hackernews.ui.theme.HackerGreen
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import com.emergetools.hackernews.ui.theme.HackerOrange
import com.emergetools.hackernews.ui.theme.HackerRed

@Composable
fun CommentsScreen(
  state: CommentsState,
  actions: (CommentsAction) -> Unit,
  navigation: (CommentsNavigation) -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(color = MaterialTheme.colorScheme.background),
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      item {
        ItemHeader(
          state = state.headerState,
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
          onLikeTapped = { item ->
            if (state is CommentsState.Content && state.loggedIn) {
              actions(
                CommentsAction.LikePost(
                  upvoted = item.upvoted,
                  url = item.upvoteUrl
                )
              )
            } else {
              navigation(CommentsNavigation.GoToLogin)
            }
          }
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
        CommentRow(
          state = comment,
          onLikeTapped = {
            if (state is CommentsState.Content && state.loggedIn) {
              actions(
                CommentsAction.LikeComment(
                  id = it.id,
                  url = it.upvoteUrl,
                  upvoted = it.upvoted
                )
              )
            } else {
              navigation(CommentsNavigation.GoToLogin)
            }
          }
        )
      }

      item {
        Spacer(modifier = Modifier.fillParentMaxHeight(fraction = 0.2f))
      }
    }
    AnimatedVisibility(
      modifier = Modifier.align(Alignment.BottomCenter),
      enter = slideInVertically { it },
      visible = state is CommentsState.Content && state.postComment != null
    ) {
      val content = state as CommentsState.Content
      val postComment = content.postComment!!
      val keyboard = LocalSoftwareKeyboardController.current
      PostCommentBump(
        modifier = Modifier.fillMaxWidth(),
        state = postComment,
        goToLogin = { navigation(CommentsNavigation.GoToLogin) },
        updateComment = { actions(CommentsAction.UpdateComment(it)) },
        submitComment = {
          actions(
            CommentsAction.PostComment(
              parentId = postComment.parentId,
              goToUrl = postComment.goToUrl,
              hmac = postComment.hmac,
              text = postComment.text
            )
          )
          keyboard?.hide()
        }
      )
    }
  }
}

@PreviewLightDark
@Composable
private fun CommentsScreenPreview() {
  HackerNewsTheme {
    CommentsScreen(
      state = CommentsState.Content(
        id = 0,
        title = "Show HN: A new HN client for Android",
        author = "rikinm",
        points = 69,
        timeLabel = "2h ago",
        body = "Hello There",
        loggedIn = false,
        upvoted = false,
        upvoteUrl = "",
        commentText = "",
        formData = null,
        comments = listOf(
          CommentState.Content(
            id = 1,
            level = 0,
            author = "rikinm",
            content = "Hello Child",
            timeLabel = "2d ago",
            upvoted = false,
            upvoteUrl = "",
            children = listOf(
              CommentState.Content(
                id = 2,
                level = 1,
                author = "vasantm",
                content = "Hello Parent",
                timeLabel = "1h ago",
                upvoted = false,
                upvoteUrl = "",
                children = listOf()
              )
            )
          )
        )
      ),
      actions = {},
      navigation = {}
    )
  }
}

@PreviewLightDark
@Composable
private fun CommentsScreenLoadingPreview() {
  HackerNewsTheme {
    CommentsScreen(
      state = CommentsState.Loading,
      actions = {},
      navigation = {}
    )
  }
}

@Composable
fun CommentRow(
  modifier: Modifier = Modifier,
  state: CommentState,
  onLikeTapped: (CommentState.Content) -> Unit
) {
  val startPadding = (state.level * 16).dp
  Column(
    modifier = modifier
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
            text = "@${state.author}",
            style = MaterialTheme.typography.labelSmall,
            color = HackerOrange,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = state.timeLabel,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
          )
          Spacer(modifier = Modifier.weight(1f))
          Box(
            modifier = Modifier
              .wrapContentSize()
              .clip(CircleShape)
              .background(
                color = if (state.upvoted) {
                  HackerGreen.copy(alpha = 0.2f)
                } else {
                  MaterialTheme.colorScheme.surfaceContainerHighest
                }
              )
              .padding(vertical = 4.dp, horizontal = 8.dp)
              .clickable { onLikeTapped(state) },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              modifier = Modifier.size(12.dp),
              imageVector = Icons.Default.ThumbUp,
              tint = if (state.upvoted) {
                HackerGreen
              } else {
                MaterialTheme.colorScheme.onSurface
              },
              contentDescription = "upvote"
            )
          }
        }
        Row {
          Text(
            text = state.content.parseAsHtml(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
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
              .background(HackerOrange)
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
    CommentRow(
      modifier = modifier,
      state = child,
      onLikeTapped = onLikeTapped
    )
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
          children = listOf(
            CommentState.Content(
              id = 2,
              level = 1,
              author = "vasantm",
              content = "Hello Child",
              timeLabel = "2h ago",
              upvoted = false,
              upvoteUrl = "",
              children = listOf()
            )
          )
        ),
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
        onLikeTapped = {}
      )
    }
  }
}

@Composable
fun ItemHeader(
  state: HeaderState,
  modifier: Modifier = Modifier,
  onLikeTapped: (HeaderState.Content) -> Unit,
) {
  Column(
    modifier = modifier
      .background(color = MaterialTheme.colorScheme.background)
      .padding(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    when (state) {
      is HeaderState.Content -> {
        Text(
          text = "@${state.author}",
          color = HackerOrange,
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = state.title,
          color = MaterialTheme.colorScheme.onSurface,
          style = MaterialTheme.typography.titleSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          MetadataTag(
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
          MetadataTag(label = state.timeLabel) {
            Icon(
              modifier = Modifier.size(12.dp),
              painter = painterResource(R.drawable.ic_time),
              tint = HackerRed,
              contentDescription = "Time Posted"
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.body != null) {
          Box(
            Modifier
              .fillMaxWidth()
              .heightIn(min = 44.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(color = MaterialTheme.colorScheme.surface)
              .padding(8.dp),
            contentAlignment = Alignment.CenterStart
          ) {
            Text(
              text = state.body.parseAsHtml(),
              color = MaterialTheme.colorScheme.onBackground,
              style = MaterialTheme.typography.labelSmall
            )
          }
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

@PreviewLightDark
@Composable
private fun ItemHeaderPreview() {
  HackerNewsTheme {
    ItemHeader(
      state = HeaderState.Content(
        id = 0L,
        title = "Show HN: A super neat HN client for Android",
        author = "rikinm",
        points = 69,
        timeLabel = "2h ago",
        body = "Hi there",
        upvoted = false,
        upvoteUrl = "",
      ),
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
      onLikeTapped = {}
    )
  }
}

@PreviewLightDark
@Composable
private fun ItemHeaderLoadingPreview() {
  HackerNewsTheme {
    ItemHeader(
      state = HeaderState.Loading,
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
      onLikeTapped = {}
    )
  }
}

@Composable
fun PostCommentBump(
  modifier: Modifier = Modifier,
  state: PostCommentState,
  goToLogin: () -> Unit,
  updateComment: (String) -> Unit,
  submitComment: () -> Unit
) {
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
      .clickable {
        if (!state.loggedIn) {
          goToLogin()
        }
      }
      .background(color = MaterialTheme.colorScheme.surfaceContainer)
      .padding(vertical = 8.dp, horizontal = 32.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        modifier = Modifier.size(12.dp),
        painter = painterResource(R.drawable.ic_chat),
        tint = MaterialTheme.colorScheme.onSurface,
        contentDescription = "Add a comment"
      )
      Text(
        text = "Add a comment",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
    TextField(
      modifier = Modifier.fillMaxWidth(),
      value = state.text,
      onValueChange = { updateComment(it) },
      enabled = state.loggedIn,
      keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Send
      ),
      keyboardActions = KeyboardActions(
        onSend = { submitComment() }
      ),
      maxLines = 4,
      shape = RoundedCornerShape(8.dp),
      colors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
      ),
    )
  }
}