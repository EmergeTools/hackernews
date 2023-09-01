package com.emergetools.hackernews.ui

import android.util.Log
import androidx.compose.runtime.Composable
import com.emergetools.hackernews.network.models.Comment
import com.emergetools.hackernews.network.models.Item
import com.emergetools.hackernews.network.models.Story
import com.emergetools.hackernews.ui.items.BuildComment
import com.emergetools.hackernews.ui.items.BuildStory

@Composable
fun BuildItem(
    index: Int,
    item: Item,
    onItemClick: (Item) -> Unit,
    onItemPrimaryButtonClick: ((Item) -> Unit)?,
) {
  when (item) {
    is Story -> {
      if (onItemPrimaryButtonClick == null) {
        throw IllegalArgumentException("Must provide a onItemPrimaryButtonClick for a Story")
      }
      BuildStory(item, index, onItemClick, onItemPrimaryButtonClick)
    }

    is Comment -> BuildComment(item)
    else -> {
      Log.d("BuildItem", "No buildItem handling for ${item.javaClass.simpleName}")
    }
  }
}