// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.kotlin.ksp) apply false
  alias(libs.plugins.roborazzi) apply false
  alias(libs.plugins.androidx.room) apply false
  alias(libs.plugins.sentry) apply false
  alias(libs.plugins.android.test) apply false
}
