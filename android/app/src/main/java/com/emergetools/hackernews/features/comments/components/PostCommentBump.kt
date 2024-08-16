package com.emergetools.hackernews.features.comments.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.R
import com.emergetools.hackernews.features.comments.PostCommentState
import com.emergetools.hackernews.ui.theme.HackerNewsTheme

@Composable
fun PostCommentBump(
  modifier: Modifier = Modifier,
  state: PostCommentState,
  goToLogin: () -> Unit,
  updateComment: (String) -> Unit,
  submitComment: () -> Unit
) {
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
      .clickable {
        if (!state.loggedIn) {
          goToLogin()
        }
      }
      .background(color = MaterialTheme.colorScheme.surfaceContainer)
      .padding(vertical = 8.dp, horizontal = 32.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        modifier = Modifier.size(12.dp),
        painter = painterResource(R.drawable.ic_chat),
        tint = MaterialTheme.colorScheme.onSurface,
        contentDescription = "Add a comment"
      )
      Text(
        text = "Add a comment",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
    TextField(
      modifier = Modifier.fillMaxWidth(),
      value = state.text,
      onValueChange = { updateComment(it) },
      enabled = state.loggedIn,
      keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Send
      ),
      keyboardActions = KeyboardActions(
        onSend = { submitComment() }
      ),
      maxLines = 4,
      shape = RoundedCornerShape(8.dp),
      colors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
      ),
    )
  }
}

@PreviewLightDark
@Composable
fun PostCommentBumpPreview() {
  HackerNewsTheme {
    PostCommentBump(
      state = PostCommentState(
        parentId = "0",
        goToUrl = "",
        hmac = "",
        loggedIn = true,
        text = "Very cool!"
      ),
      updateComment = {},
      submitComment = {},
      goToLogin = {}
    )
  }
}
