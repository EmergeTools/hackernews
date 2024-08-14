package com.emergetools.hackernews.features.stories

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emergetools.hackernews.R
import com.emergetools.hackernews.features.comments.CommentsDestinations
import com.emergetools.hackernews.ui.preview.AppStoreSnapshot
import com.emergetools.hackernews.ui.theme.HackerBlue
import com.emergetools.hackernews.ui.theme.HackerGreen
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import com.emergetools.hackernews.ui.theme.HackerOrange
import com.emergetools.hackernews.ui.theme.HackerPurple
import com.emergetools.hackernews.ui.theme.HackerRed
import com.emergetools.snapshots.annotations.EmergeAppStoreSnapshot
import me.saket.extendedspans.ExtendedSpans
import me.saket.extendedspans.SquigglyUnderlineSpanPainter
import me.saket.extendedspans.drawBehind
import me.saket.extendedspans.rememberSquigglyUnderlineAnimator

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
            ListSeparator(
              lineColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
          }
        }
      }
    }
  }
}

@PreviewLightDark
@Composable
private fun StoriesScreenPreview() {
  HackerNewsTheme {
    StoriesScreen(modifier = Modifier.fillMaxSize(), state = StoriesState(
      stories = listOf(
        StoryItem.Content(
          id = 1L,
          title = "Hello There",
          author = "heyrikin",
          score = 10,
          commentCount = 0,
          epochTimestamp = 100L,
          timeLabel = "2h ago",
          url = ""
        ),
        StoryItem.Content(
          id = 1L,
          title = "Hello There",
          author = "heyrikin",
          score = 10,
          commentCount = 0,
          epochTimestamp = 100L,
          timeLabel = "2h ago",
          url = ""
        ),
      )
    ), actions = {}, navigation = {})
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoryRow(
  modifier: Modifier = Modifier,
  item: StoryItem,
  onClick: (StoryItem.Content) -> Unit,
  onBookmark: (StoryItem.Content) -> Unit,
  onCommentClicked: (StoryItem.Content) -> Unit,
) {
  when (item) {
    is StoryItem.Content -> {
      val bookmarkHeight by animateFloatAsState(
        targetValue = if (item.bookmarked) {
          80f
        } else {
          0f
        }, animationSpec = spring(
          dampingRatio = if (item.bookmarked) {
            Spring.DampingRatioMediumBouncy
          } else {
            Spring.DampingRatioNoBouncy
          }, stiffness = Spring.StiffnessLow
        ), label = "Bookmark Height"
      )
      Column(
        modifier = modifier
          .fillMaxWidth()
          .heightIn(min = 100.dp)
          .background(color = MaterialTheme.colorScheme.background)
          .drawWithContent {
            drawContent()
            val startX = size.width * 0.75f
            val startY = 0f
            val bookmarkWidth = 50f

            val path = Path().apply {
              moveTo(startX, startY)
              lineTo(startX, startY + bookmarkHeight)
              lineTo(startX + bookmarkWidth / 2f, startY + bookmarkHeight * 0.75f)
              lineTo(startX + bookmarkWidth, startY + bookmarkHeight)
              lineTo(startX + bookmarkWidth, startY)
            }

            drawPath(
              path,
              color = HackerOrange,
            )
          }
          .combinedClickable(onClick = {
            onClick(item)
          }, onLongClick = {
            onBookmark(item)
          })
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(
          space = 8.dp,
          alignment = Alignment.CenterVertically
        )
      ) {
        Text(
          text = "@${item.author}",
          color = HackerOrange,
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = item.title,
          color = MaterialTheme.colorScheme.onSurface,
          style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(0.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          MetadataTag(
            label = "${item.score}",
          ) {
            Icon(
              modifier = Modifier.size(12.dp),
              painter = painterResource(R.drawable.ic_upvote),
              tint = HackerGreen,
              contentDescription = "Likes"
            )
          }
          MetadataTag(
            label = item.timeLabel,
          ) {
            Icon(
              modifier = Modifier.size(12.dp),
              painter = painterResource(R.drawable.ic_time_outline),
              tint = HackerPurple,
              contentDescription = "Time Posted"
            )
          }
          Spacer(modifier = Modifier.weight(1f))
          MetadataButton(
            label = "${item.commentCount}",
            onClick = { onCommentClicked(item) }
          ) {
            Icon(
              modifier = Modifier.size(12.dp),
              painter = painterResource(R.drawable.ic_chat),
              tint = HackerBlue,
              contentDescription = "Comments"
            )
          }
        }
      }
    }

    is StoryItem.Loading -> {
      val infiniteTransition = rememberInfiniteTransition("Skeleton")
      val skeletonAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
          animation = tween(durationMillis = 1000, easing = LinearEasing),
          repeatMode = RepeatMode.Reverse,
        ),
        label = "Skeleton Alpha"
      )
      Column(
        modifier = modifier
          .fillMaxWidth()
          .heightIn(min = 100.dp)
          .background(color = MaterialTheme.colorScheme.background)
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(0.15f)
            .height(12.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color = HackerOrange.copy(alpha = skeletonAlpha))
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Box(
            modifier = Modifier
              .fillMaxWidth(0.75f)
              .height(12.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
          )
          Box(
            modifier = Modifier
              .fillMaxWidth(0.5f)
              .height(12.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
          )
        }
        Spacer(modifier = Modifier.height(0.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
          ) {
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = HackerGreen.copy(alpha = skeletonAlpha))
            )
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
            )
          }
          Row(
            modifier = Modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
          ) {
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = HackerPurple.copy(alpha = skeletonAlpha))
            )
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
            )
          }
          Spacer(modifier = Modifier.weight(1f))
          Row(
            modifier = Modifier
              .wrapContentSize()
              .clip(CircleShape)
              .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
              .padding(vertical = 4.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
          ) {
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = HackerBlue.copy(alpha = skeletonAlpha))
            )
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = skeletonAlpha))
            )
          }
        }
      }
    }
  }
}

@Composable
fun ListSeparator(
  lineColor: Color,
  space: Dp = 0.5.dp
) {
  Spacer(
    modifier = Modifier
      .fillMaxWidth()
      .height(space)
      .background(color = lineColor)
  )
}

@Composable
fun MetadataButton(
  label: String,
  contentColor: Color = MaterialTheme.colorScheme.onSurface,
  backgroundColor: Color = contentColor.copy(alpha = 0.1f),
  onClick: () -> Unit = {},
  icon: @Composable () -> Unit,
) {
  Row(
    modifier = Modifier
      .wrapContentSize()
      .clip(CircleShape)
      .clickable { onClick() }
      .background(color = backgroundColor)
      .padding(vertical = 4.dp, horizontal = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically
  ) {
    icon()
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.Medium,
      color = contentColor
    )
  }
}

@Composable
fun MetadataTag(
  label: String,
  contentColor: Color = MaterialTheme.colorScheme.onSurface,
  icon: @Composable () -> Unit
) {
  Row(
    modifier = Modifier.wrapContentSize(),
    horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically
  ) {
    icon()
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.Medium,
      color = contentColor
    )
  }
}

@PreviewLightDark
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
        epochTimestamp = 100L,
        timeLabel = "2h ago",
        bookmarked = true,
        url = ""
      ),
      onClick = {},
      onBookmark = {},
      onCommentClicked = {},
    )
  }
}

@PreviewLightDark
@Composable
private fun StoryRowLoadingPreview() {
  HackerNewsTheme {
    StoryRow(
      item = StoryItem.Loading(id = 1L),
      onClick = {},
      onBookmark = {},
      onCommentClicked = {},
    )
  }
}

@Composable
fun FeedErrorCard(modifier: Modifier = Modifier, onRefresh: () -> Unit) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .height(200.dp)
      .background(color = MaterialTheme.colorScheme.background),
    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Icon(
      imageVector = Icons.Rounded.Warning,
      tint = HackerRed,
      contentDescription = "Failed to Load",
    )

    Text(
      text = "Failed to Load Feed",
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.onBackground
    )

    Button(
      colors = ButtonDefaults.buttonColors(
        containerColor = HackerRed,
        contentColor = Color.White
      ),
      onClick = { onRefresh() }) {
      Icon(
        imageVector = Icons.Rounded.Refresh,
        contentDescription = "Reload Feed"
      )
    }
  }
}

@PreviewLightDark
@Composable
fun FeedErrorCardPreview() {
  HackerNewsTheme {
    FeedErrorCard(
      onRefresh = {}
    )
  }
}

@Composable
fun FeedHeader(
  modifier: Modifier = Modifier,
  feeds: List<NewsFeed>,
  onSelected: (FeedType) -> Unit
) {
  val scrollState = rememberScrollState()

  Row(
    modifier = modifier
      .horizontalScroll(scrollState)
      .fillMaxWidth()
      .wrapContentHeight()
      .background(color = MaterialTheme.colorScheme.background),
    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically
  ) {
    feeds.forEach { feed ->
      FeedHeaderItem(state = feed, select = onSelected)
    }
  }
}

@PreviewLightDark
@Composable
fun FeedHeaderPreview() {
  HackerNewsTheme {
    FeedHeader(
      feeds = supportedFeeds,
      onSelected = {}
    )
  }
}

@Composable
fun FeedHeaderItem(state: NewsFeed, select: (FeedType) -> Unit) {
  val squigglyUnderlineAnimator = rememberSquigglyUnderlineAnimator()
  val spans = remember(state.selected) {
    ExtendedSpans(
      SquigglyUnderlineSpanPainter(
        width = if (state.selected) 2.sp else 0.sp,
        amplitude = if (state.selected) 1.sp else 0.sp,
        animator = squigglyUnderlineAnimator
      )
    )
  }
  val label = remember(state) {
    AnnotatedString(
      text = state.type.label,
      spanStyle = if (state.selected) {
        SpanStyle(textDecoration = TextDecoration.Underline)
      } else {
        SpanStyle()
      }
    )
  }

  val scale by animateFloatAsState(
    targetValue = if (state.selected) 1.0f else 0.8f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow
    ),
    label = "Selection Scale"
  )
  Text(
    modifier = Modifier
      .scale(scale)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {
        select(state.type)
      }
      .padding(8.dp)
      .drawBehind(spans),
    text = spans.extend(label),
    color = if (state.selected) HackerOrange else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
    style = MaterialTheme.typography.titleMedium,
    onTextLayout = { result ->
      spans.onTextLayout(result)
    }
  )
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
      epochTimestamp = 100L,
      timeLabel = "5h ago",
      url = ""
    ),
    StoryItem.Content(
      id = 2L,
      title = "Can we stop the decline of monarch butterflies and other pollinators?",
      author = "rbro112",
      score = 40,
      commentCount = 23,
      epochTimestamp = 100L,
      timeLabel = "2h ago",
      url = ""
    ),
    StoryItem.Content(
      id = 3L,
      title = "Andy Warhol's lost Amiga art found",
      author = "telkins",
      score = 332,
      commentCount = 103,
      epochTimestamp = 100L,
      timeLabel = "7h ago",
      url = ""
    ),
    StoryItem.Content(
      id = 4L,
      title = "A new type of neural network is more interpretable",
      author = "jshchnz",
      score = 332,
      commentCount = 37,
      epochTimestamp = 100L,
      timeLabel = "6h ago",
      url = ""
    ),
    StoryItem.Content(
      id = 5L,
      title = "Below the Root: A story, a computer game and my lifelong obsession (2015)",
      author = "sond813",
      score = 29,
      commentCount = 8,
      epochTimestamp = 100L,
      timeLabel = "2h ago",
      url = ""
    ),
    StoryItem.Content(
      id = 6L,
      title = "Replacing Liquid Metal on an Asus Zephyrus G15's CPU",
      author = "mptop27",
      score = 19,
      commentCount = 7,
      epochTimestamp = 100L,
      timeLabel = "2h ago",
      url = ""
    ),
    StoryItem.Content(
      id = 7L,
      title = "Debugging a rustc segfault on Illumos",
      author = "chromy",
      score = 301,
      commentCount = 74,
      epochTimestamp = 100L,
      timeLabel = "5h ago",
      url = ""
    ),
    StoryItem.Content(
      id = 8L,
      title = "Google loses antitrust suit over search deals on phones",
      author = "itaybre",
      score = 537,
      commentCount = 359,
      epochTimestamp = 100L,
      timeLabel = "3h ago",
      url = ""
    ),
    StoryItem.Content(
      id = 9L,
      title = "It's lights out at a cosmic restaurant",
      author = "nhinderling",
      score = 142,
      commentCount = 22,
      epochTimestamp = 100L,
      timeLabel = "4h ago",
      url = ""
    ),
    StoryItem.Content(
      id = 10L,
      title = "Uncovered Euripides fragments are 'kind of a big deal'",
      author = "sarahteng_",
      score = 332,
      commentCount = 46,
      epochTimestamp = 100L,
      timeLabel = "6h ago",
      url = ""
    ),
  )

  HackerNewsTheme {
    StoriesScreen(modifier = Modifier.fillMaxSize(), state = StoriesState(
      stories = stories
    ), actions = {}, navigation = {})
  }
}
