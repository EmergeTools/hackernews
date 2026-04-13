import io.sentry.android.gradle.tasks.SentryUploadSnapshotsTask
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.kotlin.ksp)
  alias(libs.plugins.emerge)
  alias(libs.plugins.sentry)
  alias(libs.plugins.paparazzi)
  alias(libs.plugins.androidx.room)
}

android {
  namespace = "com.emergetools.hackernews"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.emergetools.hackernews"
    minSdk = 30
    targetSdk = 36
    versionCode = 17
    versionName = "1.0.6"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  signingConfigs {
    val keystorePath = System.getenv("DECODED_KEYSTORE_PATH")
    if (keystorePath != null) {
      create("release") {
        storeFile = file(keystorePath)
        keyAlias = System.getenv("RELEASE_KEY_ALIAS")
        keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
        storePassword = System.getenv("RELEASE_STORE_PASSWORD")
      }
    }
  }

  buildTypes {
    debug {
      isDebuggable = true
      applicationIdSuffix = ".debug"
    }
    release {
      isDebuggable = false
      isMinifyEnabled = true
      isShrinkResources = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
      )
      signingConfig = signingConfigs.getByName("debug")
      enableUnitTestCoverage = false
    }
    create("playStoreRelease") {
      initWith(getByName("release"))
      signingConfig = signingConfigs.findByName("release")
    }
    create("beta") {
      initWith(getByName("release"))
      applicationIdSuffix = ".beta"
      signingConfig = signingConfigs.findByName("release")
    }
  }
  buildFeatures {
    compose = true
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }
  room {
    schemaDirectory("$projectDir/schemas")
  }
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_21
  }
}
ksp {
  arg("room.generateKotlin", "true")
}

emerge {
  vcs {
    gitHub {
      // System.getenv override is for integration tests from the emerge-android repository
      repoName.set(System.getenv("INTEGRATION_TEST_REPO_NAME") ?: "hackernews")
      repoOwner.set("EmergeTools")
    }
  }
}

sentry {
  org.set("sentry")
  projectName.set("hackernews-android")

  ignoredVariants.set(listOf("debug"))

  sizeAnalysis {
    enabled = providers.environmentVariable("GITHUB_ACTIONS").isPresent
  }

  distribution {
    enabled = providers.environmentVariable("GITHUB_ACTIONS").isPresent
    updateSdkVariants.add("beta")
  }

  snapshots {
    enabled = true
    includePrivatePreviews = true
    theme = "android:Theme.Transluscent.NoTitleBar"
  }

  debug = true
}

dependencies {

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.viewmodel)
  implementation(libs.androidx.navigation)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.shapes)
  implementation(libs.androidx.browser)
  implementation(libs.androidx.datastore)
  implementation(libs.androidx.startup)

  implementation(libs.extendedspans)

  implementation(libs.emerge.snapshots.runtime)
  implementation(libs.okhttp)
  implementation(libs.retrofit)
  implementation(libs.retrofit.kotlinx.serialization)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.jsoup)

  implementation(libs.androidx.room)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.ui.test.junit4.android)
  ksp(libs.androidx.room.compiler)

  testImplementation(libs.junit)

  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)

  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
}
