package com.emergetools.hackernews

import android.app.Application
import com.airbnb.mvrx.Mavericks

class HNApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    Mavericks.initialize(this)
  }
}
