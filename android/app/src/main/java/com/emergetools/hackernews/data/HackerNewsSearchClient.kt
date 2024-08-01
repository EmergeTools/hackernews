package com.emergetools.hackernews.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_SEARCH_URL = "https://hn.algolia.com/api/v1/"


sealed class SearchItem {
  data class Success(val item: SearchResponse) : SearchItem()
  data class Error(val message: String) : SearchItem()
}

@Serializable
data class SearchResponse(
  val id: Long,
  @SerialName("created_at")
  val createdAt: String,
  val children: List<SearchResponse>,
  val title: String? = null,
  val author: String? = null,
  val text: String? = null,
  val points: Int? = null,
)

interface HackerNewsAlgoliaApi {

  @GET("items/{id}")
  suspend fun getItem(@Path("id") itemId: Long): SearchResponse
}

class HackerNewsSearchClient(json: Json, client: OkHttpClient) {
  private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_SEARCH_URL)
    .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
    .client(client)
    .build()

  val api: HackerNewsAlgoliaApi = retrofit.create(HackerNewsAlgoliaApi::class.java)

  suspend fun getItem(itemId: Long): SearchItem {
    return withContext(Dispatchers.IO) {
      try {
        val response = api.getItem(itemId)
        SearchItem.Success(response)
      } catch (error: Exception) {
        SearchItem.Error(error.message.orEmpty())
      }
    }
  }
}
