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

@Preview(
  name = "landscape",
  device = "spec:width=411dp,height=891dp, orientation=landscape, dpi=480"
)
@Preview(
  name = "foldable",
  device = "spec:width=673dp, height=841dp, orientation=portrait, dpi=480"
)
@Preview(
  name = "tablet",
  device = "spec:width=800dp, height=1280dp, orientation=landscape, dpi=480"
)
annotation class DevicePreview

@DevicePreview
@PreviewLightDark
annotation class SnapshotPreview
