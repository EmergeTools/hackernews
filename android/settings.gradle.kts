rootProject.name = "HackerNews"

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
    google()
  }
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenLocal()
    mavenCentral()
  }
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

include(":app")
