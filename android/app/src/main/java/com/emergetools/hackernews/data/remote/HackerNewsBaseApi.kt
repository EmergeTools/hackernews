package com.emergetools.hackernews.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface HackerNewsBaseApi {
  @GET("topstories.json")
  suspend fun getTopStoryIds(): List<Long>

  @GET("newstories.json")
  suspend fun getNewStoryIds(): List<Long>

  @GET("beststories.json")
  suspend fun getBestStoryIds(): List<Long>

  @GET("askstories.json")
  suspend fun getAskStoryIds(): List<Long>

  @GET("showstories.json")
  suspend fun getShowStoryIds(): List<Long>

  @GET("jobstories.json")
  suspend fun getJobStoryIds(): List<Long>

  @GET("item/{id}.json")
  suspend fun getItem(@Path("id") itemId: Long): ItemResponse
}
