package com.emergetools.hackernews

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.emergetools.hackernews.data.local.BookmarkDao
import com.emergetools.hackernews.data.local.HackerNewsDatabase
import com.emergetools.hackernews.data.local.LocalCookieJar
import com.emergetools.hackernews.data.local.UserStorage
import com.emergetools.hackernews.data.remote.FeedIdResponse
import com.emergetools.hackernews.data.remote.HackerNewsBaseClient
import com.emergetools.hackernews.data.remote.HackerNewsSearchClient
import com.emergetools.hackernews.data.remote.HackerNewsWebClient
import com.emergetools.hackernews.data.remote.ItemResponse
import com.emergetools.hackernews.data.remote.PostPage
import com.emergetools.hackernews.features.stories.FeedType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.time.Duration

class HackerNewsApplication : Application() {
  private val json = Json { ignoreUnknownKeys = true }

  private lateinit var httpClient: OkHttpClient

  lateinit var bookmarkDao: BookmarkDao
  lateinit var userStorage: UserStorage
  lateinit var searchClient: HackerNewsSearchClient
  lateinit var webClient: HackerNewsWebClient
  lateinit var baseClient: HackerNewsBaseClient

  override fun onCreate() {
    super.onCreate()

    val db = Room.databaseBuilder(
      applicationContext,
      HackerNewsDatabase::class.java,
      "hackernews",
    ).build()
    bookmarkDao = db.bookmarkDao()

    userStorage = UserStorage(applicationContext)

    httpClient = OkHttpClient.Builder()
      .readTimeout(Duration.ofSeconds(30))
      .cookieJar(LocalCookieJar(userStorage))
      .build()

    webClient = HackerNewsWebClient(httpClient)
    baseClient = HackerNewsBaseClient(json, httpClient)

    // Find if a Android item is in the feed!
    runBlocking {
      when (val response = baseClient.getFeedIds(FeedType.Top)) {
        is FeedIdResponse.Success -> {
          // TODO: iterate through items
          // TODO: if item has Android in title go to that item
        }
        is FeedIdResponse.Error -> {
        }
      }
    }
  }

}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

fun Context.baseClient(): HackerNewsBaseClient {
  return (this.applicationContext as HackerNewsApplication).baseClient
}

fun Context.userStorage(): UserStorage {
  return (this.applicationContext as HackerNewsApplication).userStorage
}

fun Context.searchClient(): HackerNewsSearchClient {
  return (this.applicationContext as HackerNewsApplication).searchClient
}

fun Context.webClient(): HackerNewsWebClient {
  return (this.applicationContext as HackerNewsApplication).webClient
}

fun Context.bookmarkDao(): BookmarkDao {
  return (this.applicationContext as HackerNewsApplication).bookmarkDao
}
