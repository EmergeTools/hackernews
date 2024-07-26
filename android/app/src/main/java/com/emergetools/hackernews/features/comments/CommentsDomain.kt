package com.emergetools.hackernews.features.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emergetools.hackernews.data.CommentFormData
import com.emergetools.hackernews.data.CommentInfo
import com.emergetools.hackernews.data.HackerNewsSearchClient
import com.emergetools.hackernews.data.HackerNewsWebClient
import com.emergetools.hackernews.data.ItemResponse
import com.emergetools.hackernews.data.UserStorage
import com.emergetools.hackernews.data.relativeTimeStamp
import com.emergetools.hackernews.features.login.LoginDestinations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime

sealed interface CommentsState {
  val headerState: HeaderState
  val comments: List<CommentState>

  data object Loading : CommentsState {
    override val headerState: HeaderState = HeaderState.Loading
    override val comments: List<CommentState> = listOf(
      CommentState.Loading(level = 0),
      CommentState.Loading(level = 0),
    )
  }

  data class Content(
    val id: Long,
    val title: String,
    val author: String,
    val points: Int,
    val body: String?,
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

sealed interface CommentState {
  val level: Int
  val children: List<CommentState>

  data class Loading(override val level: Int) : CommentState {
    override val children: List<CommentState> = emptyList()
  }

  data class Content(
    val id: Long,
    val author: String,
    val content: String,
    val timeLabel: String,
    val upvoted: Boolean,
    val upvoteUrl: String,
    override val children: List<CommentState>,
    override val level: Int = 0,
  ) : CommentState
}

sealed interface HeaderState {
  data object Loading : HeaderState
  data class Content(
    val id: Long,
    val title: String,
    val author: String,
    val points: Int,
    val body: String?,
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
    val url: String
  ) : CommentsAction

  data class UpdateComment(val text: String) : CommentsAction

  data class PostComment(
    val parentId: String,
    val goToUrl: String,
    val hmac: String,
    val text: String
  ) : CommentsAction
}

sealed interface CommentsNavigation {
  data object GoToLogin : CommentsNavigation {
    val route = LoginDestinations.Login
  }
}

class CommentsViewModel(
  private val itemId: Long,
  private val searchClient: HackerNewsSearchClient,
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
      withContext(Dispatchers.IO) {
        val loggedIn = !userStorage.getCookie().first().isNullOrEmpty()
        val response = searchClient.api.getItem(itemId)
        val pageInfo = webClient.getPostPage(itemId)
        val comments = response.children.map { rootComment ->
          rootComment.createCommentState(0, pageInfo.commentInfoMap)
        }
        internalState.update {
          CommentsState.Content(
            id = itemId,
            title = response.title ?: "",
            author = response.author ?: "",
            points = response.points ?: 0,
            body = response.text,
            loggedIn = loggedIn,
            upvoted = pageInfo.postInfo.upvoted,
            upvoteUrl = pageInfo.postInfo.upvoteUrl,
            commentText = "",
            formData = pageInfo.commentFormData,
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
            internalState.value = current.copy(
              commentText = ""
            )
            val (parentId, gotoUrl, hmac, _, text) = current.postComment
            webClient.postComment(parentId, gotoUrl, hmac, text)
          }
        }
      }
    }
  }

  private fun ItemResponse.createCommentState(
    level: Int,
    commentInfoMap: Map<Long, CommentInfo>
  ): CommentState {
    val page = commentInfoMap[id]
    return CommentState.Content(
      id = id,
      author = author ?: "",
      content = text ?: "",
      children = children.map { child ->
        child.createCommentState(level + 1, commentInfoMap)
      },
      timeLabel = relativeTimeStamp(
        epochSeconds = OffsetDateTime.parse(createdAt).toInstant().epochSecond
      ),
      upvoted = page?.upvoted ?: false,
      upvoteUrl = page?.upvoteUrl.orEmpty(),
      level = level
    )
  }


  @Suppress("UNCHECKED_CAST")
  class Factory(
    private val itemId: Long,
    private val searchClient: HackerNewsSearchClient,
    private val webClient: HackerNewsWebClient,
    private val userStorage: UserStorage,
  ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return CommentsViewModel(itemId, searchClient, webClient, userStorage) as T
    }
  }
}
