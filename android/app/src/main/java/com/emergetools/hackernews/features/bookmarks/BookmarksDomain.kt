package com.emergetools.hackernews.features.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emergetools.hackernews.data.BookmarkDao
import com.emergetools.hackernews.data.LocalBookmark
import com.emergetools.hackernews.data.relativeTimeStamp
import com.emergetools.hackernews.features.stories.StoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookmarksState(
  val bookmarks: List<StoryItem> = emptyList()
)

class BookmarksViewModel(private val bookmarkDao: BookmarkDao) : ViewModel() {
  private val internalState = MutableStateFlow(BookmarksState())
  val state = internalState.asStateFlow()

  init {
    viewModelScope.launch {
      bookmarkDao.getAllBookmarks().filterNotNull().collect { bookmark ->
        internalState.update { current ->
          val currentBookmarks = current.bookmarks.toMutableList()
          currentBookmarks.add(bookmark.toStoryItem())
          current.copy(
            bookmarks = currentBookmarks.toList()
          )
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

fun LocalBookmark.toStoryItem(): StoryItem {
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