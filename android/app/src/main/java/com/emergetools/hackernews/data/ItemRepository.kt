package com.emergetools.hackernews.data

import android.util.Log
import com.emergetools.hackernews.data.BaseResponse.Item
import com.emergetools.hackernews.features.stories.FeedType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias ItemId = Long
typealias Page = List<ItemId>

sealed class FeedIdResponse(val page: Page) {
  class Success(page: Page) : FeedIdResponse(page)
  data class Error(val message: String) : FeedIdResponse(emptyList())
}

class ItemRepository(
  private val baseClient: HackerNewsBaseDataSource,
) {
  suspend fun getFeedIds(type: FeedType): FeedIdResponse {
    return withContext(Dispatchers.IO) {
      when (type) {
        FeedType.Top -> {
          try {
            val result = baseClient.api.getTopStoryIds()
            FeedIdResponse.Success(result)
          } catch (error: Exception) {
            FeedIdResponse.Error(error.message.orEmpty())
          }
        }

        FeedType.New -> {
          try {
            val result = baseClient.api.getNewStoryIds()
            FeedIdResponse.Success(result)
          } catch (error: Exception) {
            FeedIdResponse.Error(error.message.orEmpty())
          }
        }
      }
    }
  }

  suspend fun getPage(page: Page): List<Item> {
    Log.d("Feed", "Loading Page: $page")
    return withContext(Dispatchers.IO) {
      val result = mutableListOf<Item>()
      page.forEach { itemId ->
        try {
          val response = baseClient.api.getItem(itemId)
          if (response is Item) {
            result.add(response)
          }
        } catch (_: Exception) {
        }
      }
      result.toList()
    }
  }
}

fun MutableList<Page>.next() = removeAt(0)


