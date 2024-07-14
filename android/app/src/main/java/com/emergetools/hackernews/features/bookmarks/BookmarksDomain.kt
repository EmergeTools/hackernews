package com.emergetools.hackernews.features.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emergetools.hackernews.data.BookmarkDao
import com.emergetools.hackernews.data.LocalBookmark
import com.emergetools.hackernews.data.relativeTimeStamp
import com.emergetools.hackernews.features.stories.StoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookmarksState(
  val bookmarks: List<StoryItem> = emptyList()
)

sealed interface BookmarksAction {
  data class RemoveBookmark(val storyItem: StoryItem.Content): BookmarksAction
}

class BookmarksViewModel(private val bookmarkDao: BookmarkDao) : ViewModel() {
  private val internalState = MutableStateFlow(BookmarksState())
  val state = internalState.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      bookmarkDao.getAllBookmarks().collect { bookmarks ->
        internalState.update { current ->
          current.copy(
            bookmarks = bookmarks.map { it.toStoryItem() }
          )
        }
      }
    }
  }

  fun actions(action: BookmarksAction) {
    when (action) {
      is BookmarksAction.RemoveBookmark -> {
        viewModelScope.launch(Dispatchers.IO) {
          bookmarkDao.deleteBookmark(action.storyItem.toLocalBookmark())
        }
      }
    }
  }

  @Suppress("UNCHECKED_CAST")
  class Factory(private val bookmarkDao: BookmarkDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return BookmarksViewModel(bookmarkDao) as T
    }
  }
}

fun StoryItem.Content.toLocalBookmark(): LocalBookmark {
  return LocalBookmark(
    id = id,
    title = title,
    author = author,
    score = score,
    commentCount = commentCount,
    timestamp = epochTimestamp,
    bookmarked = true,
    url = url
  )
}

fun LocalBookmark.toStoryItem(): StoryItem.Content {
  return StoryItem.Content(
    id = this.id,
    title = this.title,
    author = this.author,
    score = this.score,
    commentCount = this.commentCount,
    bookmarked = true,
    url = this.url,
    epochTimestamp = this.timestamp,
    timeLabel = relativeTimeStamp(this.timestamp)
  )
}