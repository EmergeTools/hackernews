plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.emerge)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.serialization)
}

android {
  compileSdk = 34
  namespace = "com.emergetools.hackernews"

  defaultConfig {
    applicationId = "com.emergetools.hackernews"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
      )
      signingConfig = signingConfigs.getByName("debug")
    }
    debug {
      applicationIdSuffix = ".debug"
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
    kotlinCompilerExtensionVersion = libs.versions.compose.compiler.extension.get()
  }
}

emerge {
  // apiToken is implicitly set from the EMERGE_API_TOKEN environment variable

  performance {
    projectPath.set(":performance")
  }

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

dependencies {

  implementation(libs.accompanist.navigationanim)
  implementation(libs.accompanist.webview)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.activity.compose)
  implementation(libs.emerge.snapshots.annotations)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.kotlinx.serialization)
  implementation(libs.material.core)
  implementation(libs.material.compose.core)
  implementation(libs.material.compose.icons)
  implementation(libs.mavericks.compose)
  implementation(libs.navigation.compose.core)
  implementation(libs.navigation.compose.ktx)
  implementation(libs.okhttp)
  implementation(libs.retrofit.core)
  implementation(libs.retrofit.serialization)

  implementation(platform(libs.compose.bom))
  implementation(libs.compose.ui.tooling)
  implementation(libs.compose.ui.tooling.preview)

  debugImplementation(libs.compose.ui.test.manifest)

  testImplementation(libs.junit)

  androidTestImplementation(libs.junit)
  androidTestImplementation(libs.androidx.core)
  androidTestImplementation(libs.androidx.fragment)
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.rules)
  androidTestImplementation(libs.emerge.snapshots)
}
