package com.emergetools.hackernews

import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import sergio.sastre.composable.preview.scanner.android.AndroidComposablePreviewScanner
import sergio.sastre.composable.preview.scanner.android.AndroidPreviewInfo
import sergio.sastre.composable.preview.scanner.android.screenshotid.AndroidPreviewScreenshotIdBuilder
import sergio.sastre.composable.preview.scanner.core.preview.ComposablePreview


@RunWith(Parameterized::class)
class ComposePreviewScanner(val preview: ComposablePreview<AndroidPreviewInfo>) {

  companion object {
    // Optimization: This avoids scanning for every test
    private val cachedPreviews: List<ComposablePreview<AndroidPreviewInfo>> by lazy {
      AndroidComposablePreviewScanner()
        .scanPackageTrees("com.emergetools.hackernews")
        .getPreviews()
    }

    @JvmStatic
    @Parameterized.Parameters
    fun values(): List<ComposablePreview<AndroidPreviewInfo>> = cachedPreviews
  }

  @get:Rule val paparazzi= Paparazzi()


  @Test
  fun snapshotTest() {
    val screenshotId  = AndroidPreviewScreenshotIdBuilder(preview).build()

    paparazzi.snapshot(name = screenshotId) {
      val previewInfo = preview.previewInfo
      preview()
    }
  }


}
