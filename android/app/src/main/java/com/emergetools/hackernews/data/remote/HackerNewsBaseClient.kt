package com.emergetools.hackernews.data.remote

import android.util.Log
import com.emergetools.hackernews.data.remote.ItemResponse.Item
import com.emergetools.hackernews.features.stories.FeedType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

typealias ItemId = Long
typealias Page = List<ItemId>

private const val BASE_FIREBASE_URL = "https://hacker-news.firebaseio.com/v0/"

@Serializable(with = ItemResponseSerializer::class)
sealed interface ItemResponse {
  @Serializable
  data class Item(
    val id: Long,
    val type: String,
    val time: Long,
    val by: String? = null,
    val title: String? = null,
    val score: Int? = null,
    val url: String? = null,
    val descendants: Int? = null,
    val kids: List<Long>? = null,
    val text: String? = null
  ) : ItemResponse

  @Serializable
  data object NullResponse : ItemResponse
}

private object ItemResponseSerializer :
  JsonContentPolymorphicSerializer<ItemResponse>(ItemResponse::class) {
  override fun selectDeserializer(element: JsonElement) = when {
    element is JsonNull -> ItemResponse.NullResponse.serializer()
    else -> Item.serializer()
  }
}

sealed class FeedIdResponse(val page: Page) {
  class Success(page: Page) : FeedIdResponse(page)
  data class Error(val message: String) : FeedIdResponse(emptyList())
}

class HackerNewsBaseClient(
  json: Json,
  client: OkHttpClient,
) {
  private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_FIREBASE_URL)
    .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
    .client(client)
    .build()

  private val api = retrofit.create(HackerNewsBaseApi::class.java)

  suspend fun getFeedIds(type: FeedType): FeedIdResponse {
    return withContext(Dispatchers.IO) {
      when (type) {
        FeedType.Top -> {
          try {
            val result = api.getTopStoryIds()
            FeedIdResponse.Success(result)
          } catch (error: Exception) {
            FeedIdResponse.Error(error.message.orEmpty())
          }
        }

        FeedType.New -> {
          try {
            val result = api.getNewStoryIds()
            FeedIdResponse.Success(result)
          } catch (error: Exception) {
            FeedIdResponse.Error(error.message.orEmpty())
          }
        }

        FeedType.Ask -> {
          try {
            val result = api.getAskStoryIds()
            FeedIdResponse.Success(result)
          } catch (error: Exception) {
            FeedIdResponse.Error(error.message.orEmpty())
          }
        }

        FeedType.Show -> {
          try {
            val result = api.getShowStoryIds()
            FeedIdResponse.Success(result)
          } catch (error: Exception) {
            FeedIdResponse.Error(error.message.orEmpty())
          }
        }

        FeedType.Best -> {
          try {
            val result = api.getBestStoryIds()
            FeedIdResponse.Success(result)
          } catch (error: Exception) {
            FeedIdResponse.Error(error.message.orEmpty())
          }
        }

        FeedType.Jobs -> {
          try {
            val result = api.getJobStoryIds()
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
          val response = api.getItem(itemId)
          if (response is Item) {
            result.add(response)
          }
        } catch (_: Exception) {
        }
      }
      result.toList()
    }
  }

  suspend fun getItem(itemId: Long): ItemResponse {
    return withContext(Dispatchers.IO) {
      api.getItem(itemId)
    }
  }
}

fun MutableList<Page>.next() = removeAt(0)


