package com.emergetools.hackernews

import android.content.Context
import com.airbnb.android.showkase.models.Showkase

object ShowkaseLauncher {
    fun launch(context: Context) {
        context.startActivity(Showkase.getBrowserIntent(context))
    }

    const val isAvailable = true
}
