package dev.supergooey

import android.app.Application
import android.content.Context
import dev.supergooey.hackernews.data.HackerNewsBaseClient
import dev.supergooey.hackernews.data.HackerNewsSearchClient
import kotlinx.serialization.json.Json

class HackerNewsApplication: Application() {
  private val json = Json { ignoreUnknownKeys = true }

  val baseClient = HackerNewsBaseClient(json)
  val searchClient = HackerNewsSearchClient(json)
}

fun Context.baseClient(): HackerNewsBaseClient {
  return (this.applicationContext as HackerNewsApplication).baseClient
}

fun Context.searchClient(): HackerNewsSearchClient {
  return (this.applicationContext as HackerNewsApplication).searchClient
}
