package com.emergetools.hackernews.performance

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.emergetools.test.annotations.EmergeTest
import org.junit.runner.RunWith

private const val LAUNCH_TIMEOUT = 5000L
private const val APP_PACKAGE_NAME = "com.emergetools.hackernews"

@RunWith(AndroidJUnit4::class)
class StartupDeeplinkTest {

  @EmergeTest(spans = ["MainActivity.onCreate"])
  fun test() {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    device.pressHome()

    // Wait for launcher
    device.wait(Until.hasObject(By.pkg(device.launcherPackageName).depth(0)), LAUNCH_TIMEOUT)

    // Launch the app
    val context = InstrumentationRegistry.getInstrumentation().context
    val intent = checkNotNull(context.packageManager.getLaunchIntentForPackage(APP_PACKAGE_NAME)) {
      "Could not get launch intent for package $APP_PACKAGE_NAME"
    }

    // Clear out any previous instances
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)

    // Wait for the app to appear
    device.wait(Until.hasObject(By.pkg(APP_PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT)
  }
}
