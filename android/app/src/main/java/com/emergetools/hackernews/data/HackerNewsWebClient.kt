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

data class PostPage(
  val postInfo: PostInfo,
  val commentInfoMap: Map<Long, CommentInfo>,
  val commentFormData: CommentFormData?
)

data class PostInfo(
  val id: Long,
  val upvoted: Boolean,
  val upvoteUrl: String,
)

data class CommentInfo(
  val id: Long,
  val upvoted: Boolean,
  val upvoteUrl: String
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
      val response = httpClient.newCall(
        Request
          .Builder()
          .url("$ITEM_URL?id=$itemId")
          .build()
      ).execute()

      val document = Jsoup.parse(response.body?.string()!!)
      val itemPageInfo = document.postInfo(itemId)
      val commentPageInfoMap = document.commentInfos()
      val addCommentFormData = document.commentFormData()

      PostPage(
        postInfo = itemPageInfo,
        commentInfoMap = commentPageInfoMap,
        commentFormData = addCommentFormData
      )
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

  private fun Document.commentInfos(): Map<Long, CommentInfo> {
    val commentTree = select("table.comment-tree")
    val commentUpvoteLinks = commentTree.select("a[id^=up_]")
    return commentUpvoteLinks
      .groupBy(
        keySelector = { it.id().substring(3).toLong() },
        valueTransform = {
          CommentInfo(
            id = it.id().substring(3).toLong(),
            upvoted = it.hasClass("nosee"),
            upvoteUrl = BASE_WEB_URL + it.attr("href")
          )
        }
      ).mapValues { it.value[0] }
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
  ): Boolean {
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

      response.isSuccessful
    }
  }
}