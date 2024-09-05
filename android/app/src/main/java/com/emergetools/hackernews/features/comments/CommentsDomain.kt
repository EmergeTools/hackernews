package com.emergetools.hackernews.features.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emergetools.hackernews.data.local.UserStorage
import com.emergetools.hackernews.data.relativeTimeStamp
import com.emergetools.hackernews.data.remote.CommentFormData
import com.emergetools.hackernews.data.remote.CommentInfo
import com.emergetools.hackernews.data.remote.HackerNewsBaseClient
import com.emergetools.hackernews.data.remote.HackerNewsWebClient
import com.emergetools.hackernews.data.remote.ItemResponse
import com.emergetools.hackernews.data.remote.PostPage
import com.emergetools.hackernews.features.login.LoginDestinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

sealed interface CommentsState {
  val headerState: HeaderState
  val comments: List<CommentState>

  data object Loading : CommentsState {
    override val headerState: HeaderState = HeaderState.Loading
    override val comments: List<CommentState> = listOf(
      CommentState.Loading(level = 0),
    )
  }

  data class Content(
    val id: Long,
    val title: String,
    val author: String,
    val points: Int,
    val timeLabel: String,
    val body: BodyState,
    val loggedIn: Boolean,
    val upvoted: Boolean,
    val upvoteUrl: String,
    val commentText: String,
    val formData: CommentFormData?,
    override val comments: List<CommentState>,
  ) : CommentsState {
    override val headerState = HeaderState.Content(
      id = id,
      title = title,
      author = author,
      points = points,
      timeLabel = timeLabel,
      body = body,
      upvoted = upvoted,
      upvoteUrl = upvoteUrl
    )
    val postComment = formData?.let { data ->
      PostCommentState(
        parentId = data.parentId,
        goToUrl = data.gotoUrl,
        hmac = data.hmac,
        loggedIn = loggedIn,
        text = commentText
      )
    }
  }
}

data class PostCommentState(
  val parentId: String,
  val goToUrl: String,
  val hmac: String,
  val loggedIn: Boolean,
  val text: String,
)

enum class HiddenStatus {
  Hidden,
  HiddenByParent,
  Displayed;

  fun toggle(): HiddenStatus {
    return when (this) {
      Hidden, HiddenByParent -> Displayed
      else -> Hidden
    }
  }

  fun toggleChild(): HiddenStatus {
    return when (this) {
      Hidden, HiddenByParent -> Displayed
      else -> HiddenByParent
    }
  }
}

sealed interface CommentState {
  val level: Int
  val children: List<CommentState>
  val hidden: HiddenStatus

  data class Loading(override val level: Int) : CommentState {
    override val children: List<CommentState> = emptyList()
    override val hidden: HiddenStatus = HiddenStatus.Displayed
  }

  data class Content(
    val id: Long,
    val author: String,
    val content: String,
    val timeLabel: String,
    val upvoted: Boolean,
    val upvoteUrl: String,
    override val children: List<CommentState>,
    override val hidden: HiddenStatus = HiddenStatus.Displayed,
    override val level: Int = 0,
  ) : CommentState
}

data class BodyState(
  val text: String?,
  val collapsed: Boolean = true
)

sealed interface HeaderState {
  data object Loading : HeaderState
  data class Content(
    val id: Long,
    val title: String,
    val author: String,
    val points: Int,
    val timeLabel: String,
    val body: BodyState,
    val upvoted: Boolean,
    val upvoteUrl: String,
  ) : HeaderState
}

sealed interface CommentsAction {
  data class LikePost(
    val url: String,
    val upvoted: Boolean
  ) : CommentsAction

  data class LikeComment(
    val id: Long,
    val url: String,
    val upvoted: Boolean
  ) : CommentsAction

  data class UpdateComment(val text: String) : CommentsAction

  data class PostComment(
    val parentId: String,
    val goToUrl: String,
    val hmac: String,
    val text: String
  ) : CommentsAction

  data class ToggleHideComment(val id: Long) : CommentsAction
  data class ToggleBody(val collapse: Boolean) : CommentsAction
}

sealed interface CommentsNavigation {
  data object GoToLogin : CommentsNavigation {
    val route = LoginDestinations.Login
  }
}

class CommentsViewModel(
  private val itemId: Long,
  private val baseClient: HackerNewsBaseClient,
  private val webClient: HackerNewsWebClient,
  private val userStorage: UserStorage,
) : ViewModel() {
  private val internalState = MutableStateFlow<CommentsState>(CommentsState.Loading)
  private val loggedInFlow = userStorage.getCookie().map { !it.isNullOrEmpty() }

  val state = combine(
    loggedInFlow,
    internalState.filterIsInstance<CommentsState.Content>()
  ) { loggedIn, state ->
    if (loggedIn != state.loggedIn) {
      state.copy(
        loggedIn = loggedIn
      )
    } else {
      state
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = CommentsState.Loading
  )

  init {
    viewModelScope.launch {
      val response = baseClient.getItem(itemId)
      val postPage = webClient.getPostPage(itemId)

      if (response is ItemResponse.Item && postPage is PostPage.Success) {
        val comments = postPage.commentInfos.map { it.toCommentState() }
        val loggedIn = !userStorage.getCookie().first().isNullOrEmpty()

        internalState.update {
          CommentsState.Content(
            id = itemId,
            title = response.title ?: "",
            author = response.by ?: "",
            points = response.score ?: 0,
            timeLabel = relativeTimeStamp(
              epochSeconds = response.time
            ),
            body = BodyState(text = response.text),
            loggedIn = loggedIn,
            upvoted = postPage.postInfo.upvoted,
            upvoteUrl = postPage.postInfo.upvoteUrl,
            commentText = "",
            formData = postPage.commentFormData,
            comments = comments,
          )
        }
      }
    }
  }

  fun actions(action: CommentsAction) {
    when (action) {
      is CommentsAction.LikePost -> {
        if (!action.upvoted && action.url.isNotEmpty()) {
          val current = internalState.value
          if (current is CommentsState.Content) {
            internalState.value = current.copy(
              points = current.points + 1,
              upvoted = true
            )
          }

          viewModelScope.launch {
            webClient.upvoteItem(action.url)
          }
        }
      }

      is CommentsAction.LikeComment -> {
        if (!action.upvoted && action.url.isNotEmpty()) {
          val current = internalState.value
          if (current is CommentsState.Content) {
            internalState.value = current.copy(
              comments = internalState.value
                .comments
                .filterIsInstance<CommentState.Content>()
                .map { comment ->
                  if (comment.id == action.id) {
                    comment.copy(upvoted = true)
                  } else {
                    comment
                  }
                }
            )
          }
        }

        viewModelScope.launch {
          webClient.upvoteItem(action.url)
        }
      }

      is CommentsAction.UpdateComment -> {
        val current = internalState.value
        if (current is CommentsState.Content) {
          internalState.value = current.copy(
            commentText = action.text
          )
        }
      }

      is CommentsAction.PostComment -> {
        viewModelScope.launch {
          val current = internalState.value
          if (current is CommentsState.Content && current.postComment != null) {
            val (parentId, gotoUrl, hmac, _, text) = current.postComment
            val updatedComments = webClient.postComment(parentId, gotoUrl, hmac, text)
            internalState.value = current.copy(
              commentText = "",
              comments = updatedComments.map { it.toCommentState() }
            )
          }
        }
      }

      is CommentsAction.ToggleHideComment -> {
        fun toggleComments(
          parentId: Long,
          comments: List<CommentState.Content>
        ): List<CommentState.Content> {
          val updates = mutableListOf<CommentState.Content>()

          val parentIndex = comments.indexOfFirst { it.id == parentId }
          val parentComment = comments[parentIndex]
          updates.add(parentComment.copy(hidden = parentComment.hidden.toggle()))

          val parentLevel = parentComment.level
          var currentIndex = parentIndex + 1
          while (currentIndex <= comments.lastIndex) {
            val currentChild = comments[currentIndex]
            if (currentChild.level <= parentLevel) {
              break
            }
            updates.add(
              currentChild.copy(
                hidden = parentComment.hidden.toggleChild()
              )
            )
            currentIndex++
          }
          return updates
        }

        val currentState = internalState.value
        if (currentState is CommentsState.Content) {
          val contentComments = currentState.comments.filterIsInstance<CommentState.Content>()
          val updates = toggleComments(action.id, contentComments)
          val updatedState = currentState.copy(
            comments = contentComments.map { prev ->
              updates.find { it.id == prev.id } ?: prev
            }
          )

          internalState.compareAndSet(currentState, updatedState)
        }
      }

      is CommentsAction.ToggleBody -> {
        val currentState = internalState.value
        if (currentState is CommentsState.Content) {
          val updatedState = currentState.copy(
            body = currentState.body.copy(collapsed = action.collapse)
          )
          internalState.compareAndSet(currentState, updatedState)
        }
      }
    }
  }


  companion object {
    fun CommentInfo.toCommentState(
      now: Instant = LocalDateTime.now().toInstant(ZoneOffset.UTC),
    ): CommentState.Content {
      return CommentState.Content(
        id = id,
        author = user,
        content = text,
        timeLabel = relativeTimeStamp(
          epochSeconds = OffsetDateTime.parse(age, DateTimeFormatter.ISO_DATE_TIME)
            .toInstant().epochSecond,
          now = now.epochSecond,
        ),
        upvoted = upvoted,
        upvoteUrl = upvoteUrl,
        level = level,
        children = emptyList()
      )
    }
  }

  @Suppress("UNCHECKED_CAST")
  class Factory(
    private val itemId: Long,
    private val baseClient: HackerNewsBaseClient,
    private val webClient: HackerNewsWebClient,
    private val userStorage: UserStorage,
  ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return CommentsViewModel(itemId, baseClient, webClient, userStorage) as T
    }
  }
}
