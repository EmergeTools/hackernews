package com.emergetools.hackernews.data

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

  suspend fun getItem(id: ItemId): Item {
    return withContext(Dispatchers.IO) {
      baseClient.api.getItem(id)
    }
  }

  suspend fun getPage(page: Page): List<Item> {
    return withContext(Dispatchers.IO) {
      val result = mutableListOf<Item>()
      page.forEach { itemId ->
        val item = baseClient.api.getItem(itemId)
        result.add(item)
      }
      result.toList()
    }
  }
}

fun MutableList<Page>.next() = removeFirst()


