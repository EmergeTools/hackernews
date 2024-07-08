package dev.supergooey.hackernews.features.comments

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.supergooey.hackernews.data.HackerNewsSearchClient
import dev.supergooey.hackernews.data.ItemResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CommentsState(
  val title: String,
  val author: String,
  val points: Int,
  val comments: List<CommentState>
) {
  companion object {
    val empty = CommentsState(
      title = "",
      author = "",
      points = 0,
      comments = emptyList()
    )
  }

  val headerState = HeaderState(title, author, points)
}

data class CommentState(
  val id: Long,
  val author: String,
  val content: String,
  val children: List<CommentState>,
  val level: Int = 0,
)

data class HeaderState(
  val title: String,
  val author: String,
  val points: Int
)

class CommentsViewModel(
  private val itemId: Long,
  private val searchClient: HackerNewsSearchClient
) : ViewModel() {
  private val internalState = MutableStateFlow(CommentsState.empty)
  val state = internalState.asStateFlow()

  init {
    viewModelScope.launch {
      withContext(Dispatchers.IO) {
        val response = searchClient.api.getItem(itemId)
        val comments = response.children.map { rootComment ->
          rootComment.createCommentState(0)
        }
        internalState.update {
          CommentsState(
            title = response.title ?: "",
            author = response.author ?: "",
            points = response.points ?: 0,
            comments = comments
          )
        }
      }
    }
  }

  private fun ItemResponse.createCommentState(level: Int): CommentState {
    Log.d("Creating CommentState()", "Level: $level, Id: $id")

    return CommentState(
      id = id,
      author = author ?: "",
      content = text ?: "",
      children = children.map { child ->
        child.createCommentState(level + 1)
      },
      level = level
    )
  }


  @Suppress("UNCHECKED_CAST")
  class Factory(
    private val itemId: Long,
    private val searchClient: HackerNewsSearchClient
  ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return CommentsViewModel(itemId, searchClient) as T
    }
  }
}
