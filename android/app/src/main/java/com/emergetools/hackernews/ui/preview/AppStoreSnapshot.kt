package com.emergetools.hackernews.ui.preview

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.emergetools.snapshots.annotations.EmergeAppStoreSnapshot

@EmergeAppStoreSnapshot
@Preview(device = Devices.PIXEL_5, uiMode = UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_5)
annotation class AppStoreSnapshot

@Preview(device = Devices.PIXEL_FOLD)
@Preview(device = Devices.PIXEL_TABLET)
annotation class DevicePreview

@DevicePreview
@PreviewLightDark
annotation class SnapshotPreview
