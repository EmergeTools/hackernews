package com.emergetools.hackernews.features.comments

import com.emergetools.hackernews.data.remote.CommentInfo
import com.emergetools.hackernews.features.comments.CommentsViewModel.Companion.toCommentState
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.OffsetDateTime

class CommentsDomainTests {

  private val testNow: Instant = OffsetDateTime.parse("2024-09-05T00:00:00.000000Z").toInstant()

  @Test
  fun `toCommentState parses standard ISO 8601 format correctly`() {
    val commentInfo = CommentInfo(
      id = 1,
      user = "TestUser",
      text = "Test comment",
      age = "2024-09-04T14:42:18.000000Z",
      upvoted = false,
      upvoteUrl = "http://example.com/upvote",
      level = 0
    )

    val result = commentInfo.toCommentState(testNow)
    assertEquals("9h ago", result.timeLabel)
  }

  @Test
  fun `toCommentState parses ISO 8601 format without fractional seconds`() {
    val commentInfo = CommentInfo(
      id = 2,
      user = "AnotherUser",
      text = "Another comment",
      age = "2023-01-01T00:00:00Z",
      upvoted = true,
      upvoteUrl = "http://example.com/upvote2",
      level = 1
    )

    val result = commentInfo.toCommentState(testNow)
    assertEquals("613d ago", result.timeLabel)
  }

  @Test
  fun `toCommentState parses ISO 8601 format with offset`() {
    val commentInfo = CommentInfo(
      id = 3,
      user = "ThirdUser",
      text = "Comment with offset",
      age = "2023-06-15T10:30:00+02:00",
      upvoted = false,
      upvoteUrl = "http://example.com/upvote3",
      level = 2
    )

    val result = commentInfo.toCommentState(testNow)
    assertEquals("447d ago", result.timeLabel)
  }
}
