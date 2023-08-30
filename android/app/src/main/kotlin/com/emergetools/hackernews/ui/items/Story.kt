package com.emergetools.hackernews.ui.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.R
import com.emergetools.hackernews.network.models.Story
import com.emergetools.hackernews.ui.ALPHA_SECONDARY
import com.emergetools.hackernews.utils.msToTimeAgo

@Composable
fun BuildStory(
  story: Story,
  index: Int = 0,
  onItemClick: (Story) -> Unit,
  onItemButtonClick: (Story) -> Unit,
) {
  Row(
    modifier = Modifier
      .clickable {
        onItemClick(story)
      },
  ) {
    val storyItemModifier = Modifier.padding(top = 4.dp)

    IconButton(
      modifier = storyItemModifier
        .align(Alignment.Top),
      onClick = { /* TODO: Upvote */ },
    ) {
      Column {
        Icon(
          imageVector = Icons.Default.KeyboardArrowUp,
          contentDescription = stringResource(R.string.content_description_upvote),
          tint = MaterialTheme.colors.onBackground
        )
        Text(
          text = story.score.toString(),
          modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 2.dp),
          style = MaterialTheme.typography.caption,
          color = MaterialTheme.colors.onBackground,
          textAlign = TextAlign.Center
        )
      }
    }


    Row(
      modifier = storyItemModifier
        .padding(horizontal = 2.dp)
        .weight(1f),
    ) {

      Column(
        modifier = Modifier
          .padding(top = 2.dp)
      ) {
        val titleText = buildAnnotatedString {
          val indexString = "${index.inc()}."
          append("${index.inc()}.")
          addStyle(
            style = SpanStyle(
              color = MaterialTheme.colors.onBackground.copy(alpha = ALPHA_SECONDARY)
            ),
            start = 0,
            end = indexString.length
          )
          append(" ")
          append(story.title)
        }
        Text(
          text = titleText,
          fontWeight = FontWeight.Medium,
          style = MaterialTheme.typography.subtitle1,
          color = MaterialTheme.colors.onBackground,
        )

        Text(
          text = "(${story.displayableUrl})",
          modifier = Modifier
            .alpha(ALPHA_SECONDARY)
            .padding(top = 2.dp),
          style = MaterialTheme.typography.caption,
          color = MaterialTheme.colors.onBackground,
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(end = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            text = stringResource(
              R.string.story_item_date_author,
              story.time.msToTimeAgo(),
              story.by
            ),
            modifier = Modifier.alpha(ALPHA_SECONDARY),
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onBackground,
          )

          TextButton(
            onClick = {
              if (story.commentCount > 0) {
                onItemButtonClick(story)
              }
            },
          ) {
            Text(
              text = LocalContext.current.resources.getQuantityString(
                R.plurals.comment_count,
                story.commentCount,
                story.commentCount
              ),
              style = MaterialTheme.typography.caption,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colors.onBackground,
              textDecoration = TextDecoration.Underline
            )
          }
        }
      }
    }
  }
  Divider()
}
