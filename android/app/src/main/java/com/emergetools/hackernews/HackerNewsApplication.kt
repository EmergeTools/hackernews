package com.emergetools.hackernews

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.emergetools.hackernews.data.local.BookmarkDao
import com.emergetools.hackernews.data.local.HackerNewsDatabase
import com.emergetools.hackernews.data.local.LocalCookieJar
import com.emergetools.hackernews.data.local.UserStorage
import com.emergetools.hackernews.data.remote.HackerNewsBaseClient
import com.emergetools.hackernews.data.remote.HackerNewsSearchClient
import com.emergetools.hackernews.data.remote.HackerNewsWebClient
import io.sentry.Sentry
import io.sentry.SentryEvent
import io.sentry.SentryOptions
import io.sentry.android.core.SentryAndroid
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

    SentryAndroid.init(this) { options ->
      options.beforeSend = SentryOptions.BeforeSendCallback { event, _ ->
        if (isArtThreadDumpCrash(event)) null else event
      }
    }

    Sentry.logger().info("HackerNewsApplication#onCreate")

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
  }

  /**
   * Detects native crashes caused by ART thread dump memory corruption.
   * On certain devices (e.g. Nokia X30 5G / Android 14), ART's abort path
   * calls Thread::DumpState which invokes strlen_aarch64 on corrupted memory,
   * causing a secondary SIGSEGV. This is a device/firmware defect and not
   * actionable at the application level.
   */
  private fun isArtThreadDumpCrash(event: SentryEvent): Boolean {
    val exceptions = event.exceptions ?: return false
    for (exception in exceptions) {
      val frames = exception.stacktrace?.frames ?: continue
      val frameNames = frames.mapNotNull { it.function }
      if (frameNames.any { "strlen_aarch64" in it } &&
        frameNames.any { "DumpState" in it }) {
        return true
      }
    }
    return false
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
