package com.emergetools.hackernews.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.emergetools.hackernews.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserStorage(private val appContext: Context) {
  private val cookieKey = stringPreferencesKey("Cookie")

  suspend fun saveCookie(cookie: String) {
    appContext.dataStore.edit { store ->
      store[cookieKey] = cookie
    }
  }

  fun getCookie(): Flow<String?> {
    return appContext.dataStore.data.map { it[cookieKey] }
  }
}