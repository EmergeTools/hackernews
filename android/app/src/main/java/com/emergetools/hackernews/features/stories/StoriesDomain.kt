package com.emergetools.hackernews.features.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emergetools.hackernews.data.local.BookmarkDao
import com.emergetools.hackernews.data.relativeTimeStamp
import com.emergetools.hackernews.data.remote.FeedIdResponse
import com.emergetools.hackernews.data.remote.HackerNewsBaseClient
import com.emergetools.hackernews.data.remote.ItemResponse.Item
import com.emergetools.hackernews.data.remote.Page
import com.emergetools.hackernews.data.remote.next
import com.emergetools.hackernews.features.bookmarks.toLocalBookmark
import com.emergetools.hackernews.features.bookmarks.toStoryItem
import com.emergetools.hackernews.features.comments.CommentsDestinations
import com.emergetools.hackernews.features.stories.StoriesAction.LoadItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FeedType(val label: String) {
  Top("Top"),
  New("New")
}

enum class LoadingState {
  Loading,
  LoadingPage,
  Refreshing,
  Idle,
  Error
}

data class StoriesState(
  val stories: List<StoryItem>,
  val bookmarks: List<StoryItem.Content> = emptyList(),
  val feed: FeedType = FeedType.Top,
  val loading: LoadingState = LoadingState.Idle,
)

sealed class StoryItem(open val id: Long) {
  data class Loading(override val id: Long) : StoryItem(id)
  data class Content(
    override val id: Long,
    val title: String,
    val author: String,
    val score: Int,
    val commentCount: Int,
    val epochTimestamp: Long,
    val timeLabel: String,
    val bookmarked: Boolean = false,
    val url: String?
  ) : StoryItem(id)
}

sealed class StoriesAction {
  data object LoadItems : StoriesAction()
  data object LoadNextPage : StoriesAction()
  data object RefreshItems : StoriesAction()
  data class SelectStory(val id: Long) : StoriesAction()
  data class ToggleBookmark(val story: StoryItem.Content) : StoriesAction()
  data class SelectComments(val id: Long) : StoriesAction()
  data class SelectFeed(val feed: FeedType) : StoriesAction()
}

sealed interface StoriesNavigation {
  data class GoToStory(val closeup: StoriesDestinations.Closeup) : StoriesNavigation
  data class GoToComments(val comments: CommentsDestinations.Comments) : StoriesNavigation
}

class StoriesViewModel(
  private val baseClient: HackerNewsBaseClient,
  private val bookmarkDao: BookmarkDao
) : ViewModel() {
  private val internalState = MutableStateFlow(StoriesState(stories = emptyList()))
  val state = internalState.asStateFlow()

  // TODO: decide if this should be in the ViewModel or the Repository
  private val pages = mutableListOf<Page>()
  private var fetchJob: Job? = null

  init {
    observeBookmarks()
    actions(LoadItems)
  }

  private fun observeBookmarks() {
    viewModelScope.launch {
      bookmarkDao.getAllBookmarks().collect { bookmarks ->
        internalState.update { current ->
          current.copy(
            stories = current.stories
              .filterIsInstance<StoryItem.Content>()
              .map { story ->
                val isBookmarked = bookmarks.find { it.id == story.id } != null
                story.copy(
                  bookmarked = isBookmarked
                )
              },
            bookmarks = bookmarks.map { it.toStoryItem() },
          )
        }
      }
    }
  }

  fun actions(action: StoriesAction) {
    when (action) {
      LoadItems -> {
        pages.clear()
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
          internalState.update { it.copy(loading = LoadingState.Loading) }
          when (val response = baseClient.getFeedIds(internalState.value.feed)) {
            is FeedIdResponse.Error -> {
              delay(500)
              internalState.update { current ->
                current.copy(
                  stories = emptyList(),
                  loading = LoadingState.Error
                )
              }
            }

            is FeedIdResponse.Success -> {
              pages.addAll(
                response.page.chunked(FEED_PAGE_SIZE)
              )
              val page = pages.next()

              val newStories = fetchPage(page) {
                internalState.update { current ->
                  current.copy(
                    stories = page.map { StoryItem.Loading(it) },
                    loading = LoadingState.Loading
                  )
                }
              }

              internalState.update { current ->
                current.copy(
                  stories = newStories,
                  loading = LoadingState.Idle
                )
              }
            }
          }
        }
      }

      is StoriesAction.RefreshItems -> {
        if (internalState.value.loading !in listOf(LoadingState.Idle, LoadingState.Error)) {
          return
        }

        fetchJob?.cancel()
        pages.clear()
        fetchJob = viewModelScope.launch {
          internalState.update { current ->
            current.copy(loading = LoadingState.Refreshing)
          }
          when (val response = baseClient.getFeedIds(internalState.value.feed)) {
            is FeedIdResponse.Error -> {
              delay(500)
              internalState.update { current ->
                current.copy(loading = LoadingState.Error)
              }
            }

            is FeedIdResponse.Success -> {
              pages.addAll(response.page.chunked(FEED_PAGE_SIZE))
              val page = pages.next()
              val newStories = fetchPage(page) {}
              internalState.update { current ->
                current.copy(
                  stories = newStories,
                  loading = LoadingState.Idle
                )
              }
            }
          }
        }
      }

      is StoriesAction.SelectStory -> {
        // TODO
      }

      is StoriesAction.SelectComments -> {
        // TODO
      }

      is StoriesAction.SelectFeed -> {
        if (action.feed != state.value.feed) {
          internalState.update { current ->
            current.copy(
              feed = action.feed,
              stories = emptyList()
            )
          }
          actions(LoadItems)
        }
      }

      StoriesAction.LoadNextPage -> {
        if (pages.isNotEmpty() && internalState.value.loading == LoadingState.Idle) {
          fetchJob = viewModelScope.launch {
            val page = pages.next()
            val newStories = fetchPage(page) {
              internalState.update { current ->
                current.copy(loading = LoadingState.LoadingPage)
              }
            }

            internalState.update { current ->
              current.copy(
                stories = current.stories.subList(0, current.stories.lastIndex) + newStories,
                loading = LoadingState.Idle
              )
            }
          }
        }
      }

      is StoriesAction.ToggleBookmark -> {
        internalState.update { current ->
          current.copy(
            stories = current.stories
              .filterIsInstance<StoryItem.Content>()
              .map { story ->
                if (story == action.story) {
                  story.copy(bookmarked = !story.bookmarked)
                } else {
                  story
                }
              }
          )
        }

        viewModelScope.launch(Dispatchers.IO) {
          val bookmark = action.story.toLocalBookmark()
          if (action.story.bookmarked) {
            bookmarkDao.deleteBookmark(bookmark)
          } else {
            bookmarkDao.addBookmark(bookmark)
          }
        }
      }
    }
  }

  private suspend fun fetchPage(page: Page, onLoading: () -> Unit = {}): List<StoryItem> {
    onLoading()
    val bookmarks = internalState.value.bookmarks
    var newStories = baseClient
      .getPage(page)
      .map<Item, StoryItem> { item ->
        StoryItem.Content(
          id = item.id,
          title = item.title!!,
          author = item.by!!,
          score = item.score ?: 0,
          commentCount = item.descendants ?: 0,
          epochTimestamp = item.time,
          timeLabel = relativeTimeStamp(epochSeconds = item.time),
          bookmarked = bookmarks.find { it.id == item.id } != null,
          url = item.url
        )
      }
    if (pages.isNotEmpty()) {
      newStories = newStories + StoryItem.Loading(0L)
    }
    return newStories
  }

  @Suppress("UNCHECKED_CAST")
  class Factory(
    private val baseClient: HackerNewsBaseClient,
    private val bookmarkDao: BookmarkDao
  ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return StoriesViewModel(baseClient, bookmarkDao) as T
    }
  }
}

const val FEED_PAGE_SIZE = 20
