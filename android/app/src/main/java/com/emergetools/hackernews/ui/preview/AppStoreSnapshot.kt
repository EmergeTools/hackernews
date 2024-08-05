package com.emergetools.hackernews.ui.preview

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.emergetools.snapshots.annotations.EmergeAppStoreSnapshot

@EmergeAppStoreSnapshot
@Preview(device = Devices.PIXEL_5, uiMode = UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_5)
annotation class AppStoreSnapshot
