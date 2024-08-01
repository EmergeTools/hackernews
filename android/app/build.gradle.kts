plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.kotlin.ksp)
  alias(libs.plugins.emerge)
  alias(libs.plugins.sentry)
}

android {
  namespace = "com.emergetools.hackernews"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.emergetools.hackernews"
    minSdk = 30
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    debug {
      isDebuggable = true
      applicationIdSuffix = ".debug"
    }
    create("fast") {
      isDebuggable = false
      applicationIdSuffix = ".fast"
      signingConfig = signingConfigs.getByName("debug")
    }
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
      )
      signingConfig = signingConfigs.getByName("debug")
    }
  }
  buildFeatures {
    compose = true
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_17.toString()
  }
  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.composeCompilerExtension.get()
  }
  emerge {
    snapshots {
      tag.set("snapshot")
    }

    vcs {
      gitHub {
        repoName.set("hackernews")
        repoOwner.set("EmergeTools")
      }
    }
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
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
  implementation(libs.androidx.browser)
  implementation(libs.androidx.datastore)

  implementation(libs.okhttp)
  implementation(libs.retrofit)
  implementation(libs.retrofit.kotlinx.serialization)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.jsoup)

  implementation(libs.androidx.room)
  implementation(libs.androidx.room.ktx)
  ksp(libs.androidx.room.compiler)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  androidTestImplementation(libs.emerge.snapshots)

  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
}