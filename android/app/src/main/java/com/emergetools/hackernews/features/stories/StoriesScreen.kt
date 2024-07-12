package com.emergetools.hackernews.features.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emergetools.hackernews.R
import com.emergetools.hackernews.features.comments.CommentsDestinations
import com.emergetools.hackernews.ui.theme.HNOrange
import com.emergetools.hackernews.ui.theme.HackerNewsTheme

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
    FeedSelection(
      feedType = state.feed,
      onSelected = { actions(StoriesAction.SelectFeed(it)) }
    )
    PullToRefreshBox(
      state = pullRefreshState,
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      onRefresh = {
        actions(StoriesAction.RefreshItems)
      },
      isRefreshing = state.loading == LoadingState.Refreshing
    ) {
      LazyColumn(state = listState) {
        items(state.stories) { item ->
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
            onCommentClicked = {
              actions(StoriesAction.SelectComments(it.id))
              navigation(
                StoriesNavigation.GoToComments(
                  comments = CommentsDestinations.Comments(it.id)
                )
              )
            },
          )
        }
      }
    }
  }
}

@Preview
@Composable
private fun FeedSelectionPreview() {
  HackerNewsTheme {
    FeedSelection(
      feedType = FeedType.Top,
      onSelected = {}
    )
  }
}

@Composable
private fun FeedSelection(
  modifier: Modifier = Modifier,
  feedType: FeedType,
  onSelected: (FeedType) -> Unit,
) {
  val selectedTab = remember(feedType) { feedType.ordinal }

  TabRow(
    selectedTabIndex = selectedTab,
    modifier = modifier.wrapContentWidth(),
    containerColor = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onBackground,
    indicator = { tabPositions ->
      if (selectedTab < tabPositions.size) {
        Box(
          modifier = Modifier
            .tabIndicatorOffset(tabPositions[selectedTab])
            .height(2.dp)
            .drawBehind {
              val barWidth = size.width * 0.33f
              val start = size.center.x - barWidth / 2f
              val end = size.center.x + barWidth / 2f
              val bottom = size.height - 16f
              drawLine(
                start = Offset(start, bottom),
                end = Offset(end, bottom),
                color = HNOrange,
                strokeWidth = 4f,
                cap = StrokeCap.Round,
              )
            }
        )
      }
    },
    divider = {}
  ) {
    FeedType.entries.forEach { feedType ->
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp)
          .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
          ) {
            onSelected(feedType)
          },
        textAlign = TextAlign.Center,
        text = feedType.label,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp
      )
    }
  }
}

@Preview
@Composable
private fun StoriesScreenPreview() {
  HackerNewsTheme {
    StoriesScreen(
      modifier = Modifier.fillMaxSize(),
      state = StoriesState(
        stories = listOf(
          StoryItem.Content(
            id = 1L,
            title = "Hello There",
            author = "heyrikin",
            score = 10,
            commentCount = 0,
            timeLabel = "2h ago",
            url = ""
          ),
          StoryItem.Content(
            id = 1L,
            title = "Hello There",
            author = "heyrikin",
            score = 10,
            commentCount = 0,
            timeLabel = "2h ago",
            url = ""
          ),
        )
      ),
      actions = {},
      navigation = {}
    )
  }
}

@Preview
@Composable
private fun StoryRowPreview() {
  HackerNewsTheme {
    StoryRow(
      item = StoryItem.Content(
        id = 1L,
        title = "Hello There",
        author = "heyrikin",
        score = 10,
        commentCount = 0,
        timeLabel = "2h ago",
        url = ""
      ),
      onClick = {},
      onCommentClicked = {},
    )
  }
}

@Preview
@Composable
private fun StoryRowLoadingPreview() {
  HackerNewsTheme {
    StoryRow(
      item = StoryItem.Loading(id = 1L),
      onClick = {},
      onCommentClicked = {},
    )
  }
}

@Composable
fun StoryRow(
  modifier: Modifier = Modifier,
  item: StoryItem,
  onClick: (StoryItem.Content) -> Unit,
  onCommentClicked: (StoryItem.Content) -> Unit,
) {
  when (item) {
    is StoryItem.Content -> {
      Row(
        modifier = modifier
          .fillMaxWidth()
          .heightIn(min = 80.dp)
          .background(color = MaterialTheme.colorScheme.background)
          .clickable {
            onClick(item)
          }
          .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
          16.dp,
          alignment = Alignment.CenterHorizontally
        )
      ) {
        Column(
          modifier = Modifier
            .wrapContentHeight()
            .weight(1f),
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = item.title,
            style = MaterialTheme.typography.titleSmall
          )
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "${item.score}", style = MaterialTheme.typography.labelSmall)
            Text(text = "•", style = MaterialTheme.typography.labelSmall)
            Text(
              text = item.author,
              color = HNOrange,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Medium
            )
            Text(text = "•", style = MaterialTheme.typography.labelSmall)
            Text(text = item.timeLabel, style = MaterialTheme.typography.labelSmall)
          }
        }

        Column(
          modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable {
              onCommentClicked(item)
            },
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.ic_chat),
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = ""
          )
          Text(
            text = "${item.commentCount}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    is StoryItem.Loading -> {
      Row(
        modifier = modifier
          .fillMaxWidth()
          .heightIn(min = 80.dp)
          .background(color = MaterialTheme.colorScheme.background)
          .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
          16.dp,
          alignment = Alignment.CenterHorizontally
        )
      ) {
        Column(
          modifier = Modifier
            .wrapContentHeight()
            .weight(1f),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(0.8f)
              .height(18.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(color = Color.LightGray)
          )
          Box(
            modifier = Modifier
              .fillMaxWidth(0.45f)
              .height(18.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(color = Color.Gray)
          )
          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .width(30.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.DarkGray)
            )
            Box(
              modifier = Modifier
                .width(40.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(HNOrange)
            )
          }
        }
      }
    }
  }
}
