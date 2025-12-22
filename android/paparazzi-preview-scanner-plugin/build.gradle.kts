import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly(libs.android.gradle.build)
    implementation(libs.kotlin.gradle.plugin)

    // WARNING: Bundling Paparazzi as an implementation dependency is not ideal as it may cause
    // version conflicts if users have a different version of Paparazzi in their project.
    // However, this is necessary to automatically apply the Paparazzi plugin from our plugin.
    // Users can work around conflicts by excluding this dependency and applying Paparazzi manually.
    implementation("app.cash.paparazzi:paparazzi-gradle-plugin:2.0.0-alpha02")

    testImplementation(gradleTestKit())
    testImplementation(libs.junit)
}

gradlePlugin {
    plugins {
        create("paparazziPreviewScanner") {
            id = "com.emergetools.paparazzi.preview-scanner"
            implementationClass = "com.emergetools.paparazzi.PaparazziPreviewScannerPlugin"
            displayName = "Paparazzi Preview Scanner"
            description = "Auto-generates Paparazzi tests for Compose previews"
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}
