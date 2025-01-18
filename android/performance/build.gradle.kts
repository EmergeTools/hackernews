// This is a com.android.test project which is automatically configured by the Emerge Tools Gradle plugin:
//
// - The SDK version targets are automatically set to be identical to the app project.
// - The same build types as your app project are automatically created.
// - Test libraries including UI Automator and Junit and are automatically added as dependencies.
//
// The configuration can be modified in this file as needed.

plugins {
    id("org.jetbrains.kotlin.android")
}

dependencies {
    // Emerge's Performance Testing SDK (Required):
    implementation("com.emergetools.test:performance:2.1.1")
    // Emerge's UIAutomator helper library (Optional): https://github.com/EmergeTools/relax
    implementation("com.emergetools.test:relax:0.1.0")

    // Add additional dependencies here as needed.
    // Note Espresso is not supported as it degrades performance.
}
