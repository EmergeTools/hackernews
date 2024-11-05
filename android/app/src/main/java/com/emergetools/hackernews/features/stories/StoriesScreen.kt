package com.emergetools.hackernews.features.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.features.comments.CommentsDestinations
import com.emergetools.hackernews.features.stories.components.FeedErrorCard
import com.emergetools.hackernews.features.stories.components.FeedHeader
import com.emergetools.hackernews.ui.components.ColumnSeparator
import com.emergetools.hackernews.ui.components.StoryRow
import com.emergetools.hackernews.ui.preview.AppStoreSnapshot
import com.emergetools.hackernews.ui.preview.SnapshotPreview
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import com.emergetools.snapshots.annotations.EmergeAppStoreSnapshot
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoriesScreen(
  modifier: Modifier = Modifier,
  state: StoriesState,
  actions: (StoriesAction) -> Unit,
  navigation: (StoriesNavigation) -> Unit
) {

  fun LazyListState.atEndOfList(): Boolean {
    return layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
  }

  val listState = rememberLazyListState()
  val pullRefreshState = rememberPullToRefreshState()

  val shouldLoadMore by remember {
    derivedStateOf {
      listState.atEndOfList()
    }
  }

  LaunchedEffect(shouldLoadMore) {
    if (shouldLoadMore) {
      actions(StoriesAction.LoadNextPage)
    }
  }

  Column(
    modifier = modifier
        .graphicsLayer {
            translationY = 50f * pullRefreshState.distanceFraction
        }
        .background(color = MaterialTheme.colorScheme.background),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    FeedHeader(
      feeds = state.feeds,
      onSelected = { actions(StoriesAction.SelectFeed(it)) }
    )
    PullToRefreshBox(
      state = pullRefreshState, modifier = Modifier
            .fillMaxWidth()
            .weight(1f), onRefresh = {
        actions(StoriesAction.RefreshItems)
      }, isRefreshing = state.loading == LoadingState.Refreshing
    ) {
      LazyColumn(state = listState) {
        if (state.loading == LoadingState.Error) {
          item {
            FeedErrorCard(modifier = Modifier.animateItem()) {
              actions(StoriesAction.LoadItems)
            }
          }
        }
        itemsIndexed(state.stories) { index, item ->
          StoryRow(
            modifier = Modifier.animateItem(),
            item = item,
            onClick = {
              actions(StoriesAction.SelectStory(it.id))
              navigation(
                if (it.url != null) {
                  StoriesNavigation.GoToStory(
                    closeup = StoriesDestinations.Closeup(it.url)
                  )
                } else {
                  StoriesNavigation.GoToComments(
                    comments = CommentsDestinations.Comments(it.id)
                  )
                }
              )
            },
            onBookmark = {
              actions(StoriesAction.ToggleBookmark(it))
            },
            onCommentClicked = {
              actions(StoriesAction.SelectComments(it.id))
              navigation(
                StoriesNavigation.GoToComments(
                  comments = CommentsDestinations.Comments(it.id)
                )
              )
            },
          )
          if (index != state.stories.lastIndex) {
            ColumnSeparator(
              lineColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
          }
        }
      }
    }
  }
}

class StoriesPreviewProvider : PreviewParameterProvider<List<StoryItem>> {
  override val values: Sequence<List<StoryItem>>
    get() = sequenceOf(
      listOf(
        StoryItem.Content(
          id = 1L,
          title = "Hello There",
          author = "heyrikin",
          score = 10,
          commentCount = 0,
          epochTimestamp = Instant.now().minusSeconds(60 * 60 * 1).epochSecond,
          url = ""
        ),
      ),
      listOf(
        StoryItem.Content(
          id = 1L,
          title = "Hello There",
          author = "heyrikin",
          score = 10,
          commentCount = 0,
          epochTimestamp = Instant.now().minusSeconds(60 * 60 * 1).epochSecond,
          url = ""
        ),
        StoryItem.Content(
          id = 2L,
          title = "Hello There 2",
          author = "rbro112",
          score = 100,
          commentCount = 2,
          epochTimestamp = Instant.now().minusSeconds(60 * 60 * 2).epochSecond,
          url = ""
        ),
      )
    )
}

@SnapshotPreview
@Composable
private fun StoriesScreenPreview(
  @PreviewParameter(StoriesPreviewProvider::class) stories: List<StoryItem>,
) {
  HackerNewsTheme {
    StoriesScreen(
      modifier = Modifier.fillMaxSize(), state = StoriesState(
        stories = stories,
      ),
      actions = {},
      navigation = {})
  }
}

@OptIn(EmergeAppStoreSnapshot::class)
@AppStoreSnapshot
@Composable
private fun StoriesScreenAppStorePreview() {
  val stories = listOf(
    StoryItem.Content(
      id = 1L,
      title = "Launch HN: Airhart Aeronautics (YC S22) – A modern personal airplane",
      author = "heyrikin",
      score = 252,
      commentCount = 229,
      epochTimestamp = Instant.now().minusSeconds(60 * 60 * 5).epochSecond,
      url = ""
    ),
    StoryItem.Content(
      id = 2L,
      title = "Can we stop the decline of monarch butterflies and other pollinators?",
      author = "rbro112",
      score = 40,
      commentCount = 23,
      epochTimestamp = Instant.now().minusSeconds(60 * 60 * 2).epochSecond,
      url = ""
    ),
    StoryItem.Content(
      id = 3L,
      title = "Andy Warhol's lost Amiga art found",
      author = "telkins",
      score = 332,
      commentCount = 103,
      epochTimestamp = Instant.now().minusSeconds(60 * 60 * 7).epochSecond,
      url = ""
    ),
    StoryItem.Content(
      id = 4L,
      title = "A new type of neural network is more interpretable",
      author = "jshchnz",
      score = 332,
      commentCount = 37,
      epochTimestamp = Instant.now().minusSeconds(60 * 60 * 6).epochSecond,
      url = ""
    ),
    StoryItem.Content(
      id = 5L,
      title = "Below the Root: A story, a computer game and my lifelong obsession (2015)",
      author = "sond813",
      score = 29,
      commentCount = 8,
      epochTimestamp = Instant.now().minusSeconds(60 * 60 * 2).epochSecond,
      url = ""
    ),
    StoryItem.Content(
      id = 6L,
      title = "Replacing Liquid Metal on an Asus Zephyrus G15's CPU",
      author = "mptop27",
      score = 19,
      commentCount = 7,
      epochTimestamp = Instant.now().minusSeconds(60 * 60 * 2).epochSecond,
      url = ""
    ),
    StoryItem.Content(
      id = 7L,
      title = "Debugging a rustc segfault on Illumos",
      author = "chromy",
      score = 301,
      commentCount = 74,
      epochTimestamp = Instant.now().minusSeconds(60 * 60 * 5).epochSecond,
      url = ""
    ),
    StoryItem.Content(
      id = 8L,
      title = "Google loses antitrust suit over search deals on phones",
      author = "itaybre",
      score = 537,
      commentCount = 359,
      epochTimestamp = Instant.now().minusSeconds(60 * 60 * 3).epochSecond,
      url = ""
    ),
    StoryItem.Content(
      id = 9L,
      title = "It's lights out at a cosmic restaurant",
      author = "nhinderling",
      score = 142,
      commentCount = 22,
      epochTimestamp = 100L,
      url = ""
    ),
    StoryItem.Content(
      id = 10L,
      title = "Uncovered Euripides fragments are 'kind of a big deal'",
      author = "sarahteng_",
      score = 332,
      commentCount = 46,
      epochTimestamp = Instant.now().minusSeconds(60 * 60 * 6).epochSecond,
      url = ""
    ),
  )

  HackerNewsTheme {
    StoriesScreen(
      modifier = Modifier.fillMaxSize(),
      state = StoriesState(
        stories = stories
      ),
      actions = {},
      navigation = {}
    )
  }
}
