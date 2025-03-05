pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins {
  id("com.gradle.develocity") version("3.19.2")
}

develocity {
  buildScan {
    termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
    termsOfUseAgree.set("yes")
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()

    // We want to be able to use snapshots for the Emergetools SDK:
    maven {
      url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
      mavenContent {
        includeGroupByRegex("com.emergetools.*")
        snapshotsOnly()
      }
    }
  }
}

rootProject.name = "hacker-news"
include(":app")
