package com.emergetools

import android.app.Application
import android.content.Context
import com.emergetools.hackernews.data.HackerNewsBaseClient
import com.emergetools.hackernews.data.HackerNewsSearchClient
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.time.Duration

class HackerNewsApplication: Application() {
  private val json = Json { ignoreUnknownKeys = true }
  private val httpClient = OkHttpClient.Builder()
    .readTimeout(Duration.ofSeconds(30))
    .build()

  val baseClient = HackerNewsBaseClient(json, httpClient)
  val searchClient = HackerNewsSearchClient(json, httpClient)
}

fun Context.baseClient(): HackerNewsBaseClient {
  return (this.applicationContext as HackerNewsApplication).baseClient
}

fun Context.searchClient(): HackerNewsSearchClient {
  return (this.applicationContext as HackerNewsApplication).searchClient
}
