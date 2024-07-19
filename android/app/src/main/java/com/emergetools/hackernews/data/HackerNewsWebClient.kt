package com.emergetools.hackernews.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

const val BASE_WEB_URL = "https://news.ycombinator.com/"
private const val LOGIN_URL = BASE_WEB_URL + "login"
private const val ITEM_URL = BASE_WEB_URL + "item"

data class ItemPage(
  val id: Long,
  val upvoted: Boolean,
  val upvoteUrl: String
)

enum class LoginResponse {
  Success,
  Failed
}

class HackerNewsWebClient(
  private val httpClient: OkHttpClient,
) {
  suspend fun login(username: String, password: String): LoginResponse {
    return withContext(Dispatchers.IO) {
      val response = httpClient.newCall(
        Request.Builder()
          .url(LOGIN_URL)
          .post(
            FormBody.Builder()
              .add("acct", username)
              .add("pw", password)
              .build()
          )
          .build()
      ).execute()

      val document = Jsoup.parse(response.body?.string()!!)

      val body = document.body()
      val firstElement = body.firstChild()
      val loginFailed = firstElement?.toString()?.contains("Bad login") ?: false

      if (loginFailed) {
        LoginResponse.Failed
      } else {
        LoginResponse.Success
      }
    }
  }
  suspend fun getItemPage(itemId: Long): ItemPage {
    return withContext(Dispatchers.IO) {
      // request page
      val response = httpClient.newCall(
        Request
          .Builder()
          .url("$ITEM_URL?id=$itemId")
          .build()
      ).execute()

      val document = Jsoup.parse(response.body?.string()!!)
      val upvoteElement = document.select("#up_$itemId")
      val upvoteHref = upvoteElement.attr("href")

      ItemPage(
        id = itemId,
        upvoted = upvoteElement.hasClass("nosee"),
        upvoteUrl = BASE_WEB_URL + upvoteHref
      )
    }
  }

  suspend fun upvoteItem(url: String): Boolean {
    return withContext(Dispatchers.IO) {
      val response = httpClient.newCall(
        Request.Builder()
          .url(url)
          .build()
      ).execute()

      response.isSuccessful
    }
  }
}