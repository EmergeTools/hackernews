package com.emergetools.hackernews

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.emergetools.hackernews.data.BookmarkDao
import com.emergetools.hackernews.data.HackerNewsBaseDataSource
import com.emergetools.hackernews.data.HackerNewsDatabase
import com.emergetools.hackernews.data.HackerNewsSearchClient
import com.emergetools.hackernews.data.HackerNewsWebClient
import com.emergetools.hackernews.data.ItemRepository
import com.emergetools.hackernews.data.LocalCookieJar
import com.emergetools.hackernews.data.UserStorage
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.time.Duration

class HackerNewsApplication: Application() {
  private val json = Json { ignoreUnknownKeys = true }

  private lateinit var httpClient: OkHttpClient
  private lateinit var baseClient: HackerNewsBaseDataSource

  lateinit var bookmarkDao: BookmarkDao
  lateinit var userStorage: UserStorage
  lateinit var searchClient: HackerNewsSearchClient
  lateinit var webClient: HackerNewsWebClient
  lateinit var itemRepository: ItemRepository

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

    baseClient = HackerNewsBaseDataSource(json, httpClient)
    searchClient = HackerNewsSearchClient(json, httpClient)
    webClient = HackerNewsWebClient(httpClient)
    itemRepository = ItemRepository(baseClient)
  }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

fun Context.itemRepository(): ItemRepository {
  return (this.applicationContext as HackerNewsApplication).itemRepository
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
