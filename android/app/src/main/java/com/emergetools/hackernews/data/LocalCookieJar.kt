package com.emergetools.hackernews.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class LocalCookieJar(private val userStorage: UserStorage) : CookieJar {

  override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
    cookies.firstOrNull { it.name == "user" }?.let { authCookie ->
      runBlocking { userStorage.saveCookie(authCookie.value) }
    }
  }

  override fun loadForRequest(url: HttpUrl): List<Cookie> {
    val authCookie = runBlocking { userStorage.getCookie().first() }
    return if (authCookie != null) {
      val cookie = Cookie.Builder()
        .name("user")
        .value(authCookie)
        .domain("news.ycombinator.com")
        .build()
      listOf(cookie)
    } else {
      emptyList()
    }
  }
}