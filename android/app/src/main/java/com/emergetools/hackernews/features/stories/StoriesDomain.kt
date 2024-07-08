package com.emergetools.hackernews.features.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emergetools.hackernews.data.HackerNewsBaseClient
import com.emergetools.hackernews.features.comments.CommentsDestinations
import com.emergetools.hackernews.features.stories.StoriesAction.LoadStories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class FeedType(val label: String) {
  Top("Top"),
  New("New")
}
data class StoriesState(
  val stories: List<StoryItem>,
  val feed: FeedType = FeedType.Top

)

sealed class StoryItem(open val id: Long) {
  data class Loading(override val id: Long) : StoryItem(id)
  data class Content(
    override val id: Long,
    val title: String,
    val author: String,
    val score: Int,
    val commentCount: Int,
    val url: String?
  ) : StoryItem(id)
}

sealed class StoriesAction {
  data object LoadStories : StoriesAction()
  data class SelectStory(val id: Long) : StoriesAction()
  data class SelectComments(val id: Long) : StoriesAction()
  data class SelectFeed(val feed: FeedType) : StoriesAction()
}

// TODO(rikin): Second pass at Navigation Setup
sealed interface StoriesNavigation {
  data class GoToStory(val closeup: StoriesDestinations.Closeup) : StoriesNavigation
  data class GoToComments(val comments: CommentsDestinations.Comments) : StoriesNavigation
}

class StoriesViewModel(private val baseClient: HackerNewsBaseClient) : ViewModel() {
  private val internalState = MutableStateFlow(StoriesState(stories = emptyList()))
  val state = internalState.asStateFlow()

  init {
    actions(LoadStories)
  }

  fun actions(action: StoriesAction) {
    when (action) {
      LoadStories -> {
        viewModelScope.launch {
          withContext(Dispatchers.IO) {
            val ids = when(internalState.value.feed) {
              FeedType.Top -> {
                baseClient.api.getTopStoryIds()
              }
              FeedType.New -> {
                baseClient.api.getNewStoryIds()
              }
            }

            // now for each ID I need to load the item.
            internalState.update { current ->
              current.copy(
                stories = ids.map { StoryItem.Loading(it) }
              )
            }
            ids.forEach { id ->
              val item = baseClient.api.getItem(id)
              internalState.update { current ->
                current.copy(
                  stories = current.stories.map {
                    if (it.id == item.id) {
                      StoryItem.Content(
                        id = item.id,
                        title = item.title!!,
                        author = item.by!!,
                        score = item.score ?: 0,
                        commentCount = item.descendants ?: 0,
                        url = item.url
                      )
                    } else {
                      it
                    }
                  }
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
        internalState.update { current ->
          current.copy(
            feed = action.feed,
            stories = emptyList()
          )
        }
        actions(LoadStories)
      }
    }
  }

  @Suppress("UNCHECKED_CAST")
  class Factory(private val baseClient: HackerNewsBaseClient) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return StoriesViewModel(baseClient) as T
    }
  }
}
