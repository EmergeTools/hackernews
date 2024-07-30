package com.emergetools.hackernews.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

const val BASE_WEB_URL = "https://news.ycombinator.com/"
private const val LOGIN_URL = BASE_WEB_URL + "login"
private const val ITEM_URL = BASE_WEB_URL + "item"
private const val COMMENT_URL = BASE_WEB_URL + "comment"

sealed class PostPage {
  data class Success(
    val postInfo: PostInfo,
    val commentInfos: List<CommentInfo>,
    val commentFormData: CommentFormData?
  ) : PostPage()

  data class Error(val message: String) : PostPage()
}

data class PostInfo(
  val id: Long,
  val upvoted: Boolean,
  val upvoteUrl: String,
)

data class CommentInfo(
  val id: Long,
  val upvoted: Boolean,
  val upvoteUrl: String,
  val text: String,
  val user: String,
  val age: String,
  val level: Int,
)

data class CommentFormData(
  val parentId: String,
  val gotoUrl: String,
  val hmac: String,
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

  suspend fun getPostPage(itemId: Long): PostPage {
    return withContext(Dispatchers.IO) {
      try {
        val response = httpClient.newCall(
          Request
            .Builder()
            .url("$ITEM_URL?id=$itemId")
            .build()
        ).execute()

        val document = Jsoup.parse(response.body?.string()!!)
        val postInfo = document.postInfo(itemId)
        val commentInfos = document.commentInfos()
        val commentFormData = document.commentFormData()

        PostPage.Success(
          postInfo = postInfo,
          commentInfos = commentInfos,
          commentFormData = commentFormData
        )
      } catch (error: Exception) {
        PostPage.Error(error.message.orEmpty())
      }
    }
  }

  private fun Document.postInfo(itemId: Long): PostInfo {
    val upvoteElement = select("#up_$itemId")
    return PostInfo(
      id = itemId,
      upvoted = upvoteElement.hasClass("nosee"),
      upvoteUrl = BASE_WEB_URL + upvoteElement.attr("href")
    )
  }

  /**
   * The comment route on the website gives us a list of comments in order
   * and also gives us a "level". Now this doesn't do child/parent association
   * for us, but it does make rendering and updating comment state a lot easier.
   */
  private fun Document.commentInfos(): List<CommentInfo> {
    val commentTree = select("table.comment-tree")
    val comments = commentTree.select("tr.athing.comtr")
    val infos = comments.map { commentElement ->
      val id = commentElement.id().toLong()
      val level = commentElement.select("td.ind").attr("indent").toInt()
      val text = commentElement.select("div.commtext").text()
      val user = commentElement.select("a.hnuser").text()
      val time = commentElement.select("span.age").attr("title")
      val upvoteLink = commentElement.select("a[id^=up_]")
      val url = BASE_WEB_URL + upvoteLink.attr("href")
      val upvoted = upvoteLink.hasClass("nosee")

      CommentInfo(
        id = id,
        user = user,
        age = time,
        text = text,
        level = level,
        upvoteUrl = url,
        upvoted = upvoted
      )
    }

    return infos
  }

  private fun Document.commentFormData(): CommentFormData? {
    val commentFormElement = select("form[action=comment]")
    if (commentFormElement.isEmpty()) {
      return null
    }

    val parentId = commentFormElement.select("input[name=parent]").attr("value")
    val goto = commentFormElement.select("input[name=goto]").attr("value")
    val hmac = commentFormElement.select("input[name=hmac]").attr("value")
    return CommentFormData(
      parentId = parentId,
      gotoUrl = goto,
      hmac = hmac,
    )
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

  suspend fun postComment(
    parentId: String,
    gotoUrl: String,
    hmac: String,
    text: String
  ): List<CommentInfo> {
    return withContext(Dispatchers.IO) {
      val response = httpClient.newCall(
        Request.Builder()
          .url(COMMENT_URL)
          .post(
            FormBody.Builder()
              .add("parent", parentId)
              .add("goto", gotoUrl)
              .add("hmac", hmac)
              .add("text", text)
              .build()
          )
          .build()
      ).execute()

      val document = Jsoup.parse(response.body?.string()!!)
      document.commentInfos()
    }
  }
}