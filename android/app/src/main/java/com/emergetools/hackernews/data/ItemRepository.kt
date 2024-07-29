package com.emergetools.hackernews.data

import com.emergetools.hackernews.data.BaseResponse.Item
import com.emergetools.hackernews.features.stories.FeedType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias ItemId = Long
typealias Page = List<ItemId>

class ItemRepository(
  private val baseClient: HackerNewsBaseDataSource,
) {
  suspend fun getFeedIds(type: FeedType): Page {
    return withContext(Dispatchers.IO) {
      when (type) {
        FeedType.Top -> {
          baseClient.api.getTopStoryIds()
        }

        FeedType.New -> {
          baseClient.api.getNewStoryIds()
        }
      }
    }
  }

  suspend fun getPage(page: Page): List<Item> {
    return withContext(Dispatchers.IO) {
      val result = mutableListOf<Item>()
      page.forEach { itemId ->
        val response = baseClient.api.getItem(itemId)
        if (response is Item) {
          result.add(response)
        }
      }
      result.toList()
    }
  }
}

fun MutableList<Page>.next() = removeAt(0)


