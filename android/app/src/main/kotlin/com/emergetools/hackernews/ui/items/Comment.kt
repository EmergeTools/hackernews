package com.emergetools.hackernews.ui.items

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.network.models.Comment
import com.emergetools.hackernews.ui.BuildItem
import com.emergetools.hackernews.ui.annotations.SnapshotTestingPreviews

@Composable
fun BuildComment(
  comment: Comment,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp),
  ) {
    Text(
      text = comment.text,
      style = MaterialTheme.typography.body1,
      color = MaterialTheme.colors.onBackground,
    )
  }
  Divider()
}

/**
 * Example generated snapshot test from main source set.
 * To generate a snapshot test for this preview, add the snapshot-processor as a ksp dependency.
 */
@SnapshotTestingPreviews
@Preview
@Composable
fun CommentRow() {
  val mockComment = Comment(
    id = 1,
    text = "This is a mock comment I wrote for the test",
    time = 0,
    by = "Ryan B",
    parent = null,
    replies = emptyList(),
  )
  BuildItem(
    item = mockComment,
    onItemClick = {},
    onItemPrimaryButtonClick = null,
    index = 0,
  )
}
