package com.emergetools.hackernews.features.comments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.features.comments.components.CommentRow
import com.emergetools.hackernews.features.comments.components.CommentsHeader
import com.emergetools.hackernews.features.comments.components.PostCommentBump
import com.emergetools.hackernews.ui.preview.SnapshotPreview
import com.emergetools.hackernews.ui.theme.HackerNewsTheme

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
      contentPadding = PaddingValues(8.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      item {
        CommentsHeader(
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
          },
          onToggleBody = { actions(CommentsAction.ToggleBody(it)) }
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
                intervals = floatArrayOf(10f, 10f)
              )
            )
          }
        )
      }
      items(items = state.comments.filter { it.hidden != HiddenStatus.HiddenByParent }) { comment ->
        CommentRow(
          state = comment,
          onToggleHide = {
            actions(CommentsAction.ToggleHideComment(it.id))
          },
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

@SnapshotPreview
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
        body = BodyState("Hello There"),
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
            children = listOf()
          ),
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
      ),
      actions = {},
      navigation = {}
    )
  }
}

@SnapshotPreview
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

