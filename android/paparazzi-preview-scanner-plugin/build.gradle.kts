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
