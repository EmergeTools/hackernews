package com.emergetools.hackernews.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URI
import java.net.URISyntaxException

@Serializable
sealed class Item {
  abstract val id: Long
  abstract val by: String
  abstract val time: Long

  val deleted: Boolean = false
  val dead: Boolean = false
}

@Serializable
@SerialName("story")
data class Story(
  override val id: Long,
  override val by: String,
  override val time: Long,

  val title: String,
  val text: String? = null,
  val url: String,
  val score: Int,
  val descendants: Int,
  @SerialName("kids") val comments: List<Long>,
) : Item() {

  val commentCount: Int
    get() = comments.size

  val displayableUrl: String
    get() {
      return try {
        val uri = URI(url)
        // TODO: First path for github
        uri.host.removePrefix("www.")
      } catch (e: URISyntaxException) {
        url
      }
    }
}

@Serializable
@SerialName("comment")
data class Comment(
  override val id: Long,
  override val by: String,
  override val time: Long,

  val text: String,
  val parent: Long?,
  @SerialName("kids") val replies: List<Long>,
) : Item()

@Serializable
@SerialName("job")
data class Job(
  override val id: Long,
  override val by: String,
  override val time: Long,

  val title: String,
  val score: Int,
  val text: String? = null,
) : Item()

@Serializable
@SerialName("poll")
data class Poll(
  override val id: Long,
  override val by: String,
  override val time: Long,

  val title: String,
  val score: Int,
  val descendants: Int,
  @SerialName("kids") val comments: List<Long>,
  @SerialName("parts") val pollopts: List<Long> = emptyList(),
) : Item()

@Serializable
@SerialName("pollopt")
data class Pollopt(
  override val id: Long,
  override val by: String,
  override val time: Long,

  val poll: Long,
  val score: Int,
  val text: String? = null,
) : Item()
