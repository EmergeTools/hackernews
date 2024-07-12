package com.emergetools.hackernews.data

import java.time.Instant

/**
 * Convert the difference between two epoch second times
 * to a relative timestamp label like "2m ago" or "5d ago"
 */
fun relativeTimeStamp(epochSeconds: Long): String {
  val now = Instant.now().epochSecond
  val difference = now - epochSeconds

  val minutes = difference / SECONDS_IN_MINUTE
  if (minutes < 60) {
    return "${minutes.toInt()}m ago"
  }

  val hours = minutes / MINUTES_IN_HOUR
  if (hours < 24) {
    return "${hours.toInt()}h ago"
  }

  val days = hours / HOURS_IN_DAY
  return "${days.toInt()}d ago"
}

private const val SECONDS_IN_MINUTE = 60.0
private const val MINUTES_IN_HOUR = 60.0
private const val HOURS_IN_DAY = 24.0
