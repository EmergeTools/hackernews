rootProject.name = "paparazzi-preview-scanner-plugin"

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
  versionCatalogs.create("libs") { from(files("../gradle/libs.versions.toml")) }

  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
}
