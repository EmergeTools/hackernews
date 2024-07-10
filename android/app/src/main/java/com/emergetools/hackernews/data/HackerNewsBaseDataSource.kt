package com.emergetools.hackernews.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

const val BASE_FIREBASE_URL = "https://hacker-news.firebaseio.com/v0/"

@Serializable
data class Item(
  val id: Long,
  val type: String,
  val by: String? = null,
  val title: String? = null,
  val score: Int? = null,
  val url: String? = null,
  val descendants: Int? = null,
  val kids: List<Long>? = null,
  val text: String? = null
)

interface HackerNewsBaseApi {
  @GET("topstories.json")
  suspend fun getTopStoryIds(): List<Long>

  @GET("newstories.json")
  suspend fun getNewStoryIds(): List<Long>

  @GET("item/{id}.json")
  suspend fun getItem(@Path("id") itemId: Long): Item
}

class HackerNewsBaseDataSource(json: Json, client: OkHttpClient) {
  private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_FIREBASE_URL)
    .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
    .client(client)
    .build()

  val api = retrofit.create(HackerNewsBaseApi::class.java)
}