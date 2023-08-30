package com.emergetools.hackernews.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.emergetools.hackernews.R

const val MILLIS_IN_SEC = 1000
const val SEC_IN_MIN = 60
const val SEC_IN_HOUR = 3600
const val SEC_IN_DAY = 86400
const val SEC_IN_WEEK = 604800
const val SEC_IN_MONTH = 2_628_000
const val SEC_IN_YEAR = 31_536_000

@Composable
fun Long.msToTimeAgo(): String {
  val secondsAgo = (System.currentTimeMillis() / MILLIS_IN_SEC) - this

  return when {
    secondsAgo < SEC_IN_MIN -> LocalContext.current.resources.getQuantityString(
      R.plurals.seconds_ago,
      secondsAgo.toInt(),
      secondsAgo
    )

    secondsAgo < SEC_IN_HOUR -> {
      val minutes = secondsAgo / SEC_IN_MIN
      LocalContext.current.resources.getQuantityString(
        R.plurals.minutes_ago,
        minutes.toInt(),
        minutes
      )
    }

    secondsAgo < SEC_IN_DAY -> {
      val hours = secondsAgo / SEC_IN_HOUR
      LocalContext.current.resources.getQuantityString(
        R.plurals.hours_ago,
        hours.toInt(),
        hours
      )
    }

    secondsAgo < SEC_IN_WEEK -> {
      val days = secondsAgo / SEC_IN_DAY
      LocalContext.current.resources.getQuantityString(
        R.plurals.days_ago,
        days.toInt(),
        days
      )
    }

    secondsAgo < SEC_IN_MONTH -> {
      val weeks = secondsAgo / SEC_IN_WEEK
      LocalContext.current.resources.getQuantityString(
        R.plurals.weeks_ago,
        weeks.toInt(),
        weeks
      )
    }

    secondsAgo < SEC_IN_YEAR -> {
      val months = secondsAgo / SEC_IN_MONTH
      LocalContext.current.resources.getQuantityString(
        R.plurals.months_ago,
        months.toInt(),
        months
      )
    }

    else -> {
      val years = secondsAgo / SEC_IN_YEAR
      LocalContext.current.resources.getQuantityString(
        R.plurals.years_ago,
        years.toInt(),
        years.toInt()
      )
    }
  }
}