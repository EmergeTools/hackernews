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
import com.emergetools.hackernews.ui.preview.AppStoreSnapshot
import com.emergetools.hackernews.ui.preview.SnapshotPreview
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import com.emergetools.snapshots.annotations.EmergeAppStoreSnapshot
import java.time.Instant

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
        epochSeconds = Instant.now().minusSeconds(60 * 60 * 2).epochSecond,
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
            age = "2024-09-05T17:48:25",
            upvoted = false,
            upvoteUrl = "",
            children = listOf()
          ),
          CommentState.Content(
            id = 2,
            level = 1,
            author = "vasantm",
            content = "Hello Parent",
            age = "2024-09-05T17:48:25",
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

@OptIn(EmergeAppStoreSnapshot::class)
@AppStoreSnapshot
@Composable
fun CommentsScreenAppStorePreview() {
  HackerNewsTheme {
    CommentsScreen(
      state = CommentsState.Content(
        id = 0,
        title = "Why is the Oral-B iOS app almost 300 MB? And why is Colgate's app even bigger..?",
        author = "rbro112",
        points = 155,
        epochSeconds = Instant.now().minusSeconds(60 * 60 * 6).epochSecond,
        body = BodyState(null),
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
            content = "My question is: why do an Oral-B app and a Colgate app even exist?",
            age = "2024-09-05T17:48:25",
            upvoted = false,
            upvoteUrl = "",
            children = listOf()
          ),
          CommentState.Content(
            id = 2,
            level = 1,
            author = "telkins",
            content = "The same reason they now have a toothbrush with AI. Because they are in a race to continuously re-invent the toothbrush every year to create new USPs, create new marketing angles and keep sales high.",
            age = "2024-09-05T17:48:25",
            upvoted = false,
            upvoteUrl = "",
            children = listOf()
          ),
          CommentState.Content(
            id = 3,
            level = 2,
            author = "nhinderling",
            content = "I'm surprised that health insurance companies haven't started offering \"good brusher\" discounts the way car insurance companies offer a \"good driver\" discount when you use their car data logging device/app.",
            age = "2024-09-05T17:48:25",
            upvoted = false,
            upvoteUrl = "",
            children = listOf()
          ),
          // TODO
          CommentState.Content(
            id = 4,
            level = 0,
            author = "vasantm",
            content = "So it’s all just stupidly big PDFs used as images of the different models?\n" +
              "Not what I was expecting. I was expecting it to be more like the Colgate app mentioned later in thread.",
            age = "2024-09-05T17:48:25",
            upvoted = false,
            upvoteUrl = "",
            children = listOf()
          ),
          CommentState.Content(
            id = 5,
            level = 1,
            author = "itaybre",
            content = "I wonder if they generated the PDFs with a web browser's Print to PDF feature. Chrome's PDFs are massive.",
            age = "2024-09-05T17:48:25",
            upvoted = false,
            upvoteUrl = "",
            children = listOf()
          ),
          CommentState.Content(
            id = 6,
            level = 1,
            author = "chromy",
            content = "That’s insane. They should absolutely, 100% be pulled on demand.",
            age = "2024-09-05T17:48:25",
            upvoted = false,
            upvoteUrl = "",
            children = listOf()
          ),
          CommentState.Content(
            id = 7,
            level = 2,
            author = "mptop27",
            content = "I think there’s nothing wrong with shipping the images, why should you need an Internet connection to pair your toothbrush (which of your using the app you must want to do).\n" +
              "But why can’t they be vector images? The pictures in the tweet looks like they could easily be replaced by vectors and the difference would be nearly unnoticeable.",
            age = "2024-09-05T17:48:25",
            upvoted = false,
            upvoteUrl = "",
            children = listOf()
          ),
        )
      ),
      actions = {},
      navigation = {}
    )
  }
}
