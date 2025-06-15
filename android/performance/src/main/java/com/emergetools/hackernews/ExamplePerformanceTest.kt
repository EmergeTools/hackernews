package com.emergetools.hackernews

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.emergetools.test.annotations.EmergeInit
import com.emergetools.test.annotations.EmergeSetup
import com.emergetools.test.annotations.EmergeTest

private const val LAUNCH_TIMEOUT = 5000L
private const val APP_PACKAGE_NAME = "com.emergetools.hackernews"

/**
 * An example performance test class.
 *
 * Performance test classes can have multiple tests, but tests in a given class share @EmergeInit and @EmergeSetup
 * methods. For tests that require a different init or setup multiple test classes are supported.
 *
 * Note that each test (ie. each method annotated with @EmergeTest) will be run on a separate device, they cannot
 * impact each other in any way.
 */
class ExamplePerformanceTest {

    @EmergeInit
    fun init() {
        // OPTIONAL
        // Runs just once after installing the app on the test device before any other method.
        // Typically this is used to log into the app, if needed.
        // Only one @EmergeInit method per class is supported.
    }

    @EmergeSetup
    fun setup() {
        // OPTIONAL
        // Runs once before each test iteration.
        // Typically this is used to navigate through to the screen where the performance test is meant to begin.
        // Only one @EmergeSetup method per class is supported.
    }

    @EmergeTest
    fun myPerformanceTest() {
        // REQUIRED
        // The performance test. This is where the app should go through a short flow whose performance is critical.
        // This might involve launching a screen or any other operation supported by UI Automator.
        // As an example here we launch the application from the home screen.

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()

        // Wait for launcher
        device.wait(Until.hasObject(By.pkg(device.launcherPackageName).depth(0)), LAUNCH_TIMEOUT)

        // Launch the app
        val context = ApplicationProvider.getApplicationContext<Context>()
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
