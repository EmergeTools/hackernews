package com.emergetools.hackernews.network

import com.emergetools.hackernews.network.models.Item
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

private const val HN_BASE_URL = "https://hacker-news.firebaseio.com/v0/"

private val retrofit = Retrofit.Builder()
  .baseUrl(HN_BASE_URL)
  .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
  .build()

interface HNApiService {

  @GET("topstories.json")
  suspend fun getTopStories(): List<Long>

  @GET("item/{id}.json")
  suspend fun getItem(@Path("id") id: Long): Item
}

object HNApi {
  val retrofitService: HNApiService by lazy {
    retrofit.create(HNApiService::class.java)
  }
}
