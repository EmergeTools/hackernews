package com.emergetools.hackernews.features.comments

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emergetools.hackernews.data.HackerNewsSearchClient
import com.emergetools.hackernews.data.HackerNewsWebClient
import com.emergetools.hackernews.data.ItemPage
import com.emergetools.hackernews.data.ItemResponse
import com.emergetools.hackernews.data.relativeTimeStamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime

sealed interface CommentsState {
  val headerState: HeaderState
  val comments: List<CommentState>

  data object Loading: CommentsState {
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
    val text: String?,
    val page: ItemPage,
    override val comments: List<CommentState>,
  ): CommentsState {
    override val headerState = HeaderState.Content(
      title,
      author,
      points,
      page.upvoted,
      text,
    )
  }
}

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
    override val children: List<CommentState>,
    override val level: Int = 0,
  ): CommentState
}

sealed interface HeaderState {
  data object Loading: HeaderState
  data class Content(
    val title: String,
    val author: String,
    val points: Int,
    val upvoted: Boolean,
    val body: String?,
  ): HeaderState
}

sealed interface CommentsAction {
  data object LikePostTapped: CommentsAction
}

class CommentsViewModel(
  private val itemId: Long,
  private val searchClient: HackerNewsSearchClient,
  private val webClient: HackerNewsWebClient
) : ViewModel() {
  private val internalState = MutableStateFlow<CommentsState>(CommentsState.Loading)
  val state = internalState.asStateFlow()

  init {
    viewModelScope.launch {
      withContext(Dispatchers.IO) {
        val response = searchClient.api.getItem(itemId)
        val page = webClient.getItemPage(itemId)
        Log.d("CommentsViewModel", "Item Page: $page")
        val comments = response.children.map { rootComment ->
          rootComment.createCommentState(0)
        }
        internalState.update {
          CommentsState.Content(
            id = itemId,
            title = response.title ?: "",
            author = response.author ?: "",
            points = response.points ?: 0,
            text = response.text,
            page = page,
            comments = comments,
          )
        }
      }
    }
  }

  fun actions(action: CommentsAction) {
    when (action) {
      CommentsAction.LikePostTapped -> {
        Log.d("CommentsViewModel", "Post Liked: $itemId")
        val current = internalState.value
        if (current is CommentsState.Content && !current.page.upvoted && current.page.upvoteUrl.isNotEmpty()) {
          // eager ui update
          internalState.value = current.copy(
            points = current.points + 1,
            page = current.page.copy(
              upvoted = true
            )
          )
          viewModelScope.launch {
            val success = webClient.upvoteItem(current.page.upvoteUrl)
            if (success) {
              val refreshedPage = webClient.getItemPage(itemId)
              Log.d("CommentsViewModel", "Refreshed Item Page: $refreshedPage")
            }
          }
        }
      }
    }
  }

  private fun ItemResponse.createCommentState(level: Int): CommentState {
    Log.d("Creating CommentState()", "Level: $level, Id: $id")

    return CommentState.Content(
      id = id,
      author = author ?: "",
      content = text ?: "",
      children = children.map { child ->
        child.createCommentState(level + 1)
      },
      timeLabel = relativeTimeStamp(
        epochSeconds = OffsetDateTime.parse(createdAt).toInstant().epochSecond
      ),
      level = level
    )
  }


  @Suppress("UNCHECKED_CAST")
  class Factory(
    private val itemId: Long,
    private val searchClient: HackerNewsSearchClient,
    private val webClient: HackerNewsWebClient,
  ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return CommentsViewModel(itemId, searchClient, webClient) as T
    }
  }
}
