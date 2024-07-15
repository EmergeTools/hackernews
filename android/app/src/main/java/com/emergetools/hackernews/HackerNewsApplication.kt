package com.emergetools.hackernews

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.emergetools.hackernews.data.BookmarkDao
import com.emergetools.hackernews.data.HackerNewsBaseDataSource
import com.emergetools.hackernews.data.HackerNewsDatabase
import com.emergetools.hackernews.data.HackerNewsSearchClient
import com.emergetools.hackernews.data.ItemRepository
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.time.Duration

class HackerNewsApplication: Application() {
  private val json = Json { ignoreUnknownKeys = true }
  private val httpClient = OkHttpClient.Builder()
    .readTimeout(Duration.ofSeconds(30))
    .build()

  private val baseClient = HackerNewsBaseDataSource(json, httpClient)
  val searchClient = HackerNewsSearchClient(json, httpClient)
  val itemRepository = ItemRepository(baseClient)

  lateinit var bookmarkDao: BookmarkDao

  override fun onCreate() {
    super.onCreate()

    val db = Room.databaseBuilder(
      applicationContext,
      HackerNewsDatabase::class.java,
      "hackernews",
    ).build()

    bookmarkDao = db.bookmarkDao()
  }
}

fun Context.itemRepository(): ItemRepository {
  return (this.applicationContext as HackerNewsApplication).itemRepository
}

fun Context.searchClient(): HackerNewsSearchClient {
  return (this.applicationContext as HackerNewsApplication).searchClient
}

fun Context.bookmarkDao(): BookmarkDao {
  return (this.applicationContext as HackerNewsApplication).bookmarkDao
}
