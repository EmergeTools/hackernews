rootProject.name = "HackerNews"

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenLocal()
    google()
  }
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
  }
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

include(
  ":app",
  ":performance"
)
