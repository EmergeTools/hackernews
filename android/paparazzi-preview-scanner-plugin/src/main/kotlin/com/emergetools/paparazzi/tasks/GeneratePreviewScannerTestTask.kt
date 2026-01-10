package com.emergetools.paparazzi.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.PathSensitivity.RELATIVE
import org.gradle.api.tasks.TaskAction
import org.intellij.lang.annotations.Language
import java.io.File

@CacheableTask
abstract class GeneratePreviewScannerTestTask : DefaultTask() {

    @get:Input
    abstract val scanPackages: ListProperty<String>

    @get:Input
    abstract val namespace: Property<String>

    @get:Input
    abstract val includePrivatePreviews: Property<Boolean>

    @get:InputFiles
    @get:PathSensitive(RELATIVE)
    abstract val sourceDirs: ListProperty<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        logger.lifecycle("Generating Paparazzi preview scanner test...")
        val packages = scanPackages.get().ifEmpty {
            // Default to namespace if no packages specified
            listOf(namespace.get())
        }

        val sources = sourceDirs.get()
        logger.lifecycle("Scanning packages: $packages")
        logger.lifecycle("Source directories: $sources")
        val packageName = "com.emergetools.paparazzi.generated"
        val className = "ComposePreviewScannerTest"

        val testContent = generateTestFile(
            packageName,
            className,
            packages,
            includePrivatePreviews.getOrElse(false),
            sources
        )

        // Clean output directory before generating new content to prevent stale outputs
        val outputDir = outputDirectory.get().asFile
        if (outputDir.exists()) {
            logger.lifecycle("Cleaning output directory: ${outputDir.absolutePath}")
            outputDir.deleteRecursively()
        }
        val packageDir = File(outputDir, packageName.replace('.', '/'))
        packageDir.mkdirs()

        val testFile = File(packageDir, "$className.kt")
        testFile.writeText(testContent)

        logger.lifecycle("Generated Paparazzi test at: ${testFile.absolutePath}")
    }

    private fun generateTestFile(
        packageName: String,
        className: String,
        scanPackages: List<String>,
        includePrivatePreviews: Boolean,
        sourceDirs: List<String>
    ): String {
        val packagesString = scanPackages.joinToString(", ") { "\"$it\"" }
        val sourceDirsString = sourceDirs.joinToString(", ") { "\"$it\"" }

        return """
            package $packageName

            import android.content.res.Configuration.UI_MODE_NIGHT_MASK
            import android.content.res.Configuration.UI_MODE_NIGHT_YES
            import androidx.compose.ui.graphics.Color
            import androidx.compose.ui.unit.dp
            import androidx.compose.ui.Modifier
            import androidx.compose.foundation.background
            import androidx.compose.foundation.layout.size
            import androidx.compose.foundation.layout.Box
            import androidx.compose.runtime.Composable
            import app.cash.paparazzi.detectEnvironment
            import app.cash.paparazzi.DeviceConfig
            import app.cash.paparazzi.HtmlReportWriter
            import app.cash.paparazzi.Paparazzi
            import app.cash.paparazzi.Snapshot
            import app.cash.paparazzi.SnapshotHandler
            import app.cash.paparazzi.SnapshotVerifier
            import app.cash.paparazzi.TestName
            import com.android.ide.common.rendering.api.SessionParams
            import com.android.resources.*
            import kotlin.math.ceil
            import org.junit.Rule
            import org.junit.Test
            import org.junit.runner.RunWith
            import org.junit.runners.Parameterized
            import sergio.sastre.composable.preview.scanner.android.AndroidComposablePreviewScanner
            import sergio.sastre.composable.preview.scanner.android.AndroidPreviewInfo
            import sergio.sastre.composable.preview.scanner.android.device.DevicePreviewInfoParser
            import sergio.sastre.composable.preview.scanner.android.device.domain.Device
            import sergio.sastre.composable.preview.scanner.android.device.types.DEFAULT
            import sergio.sastre.composable.preview.scanner.android.screenshotid.AndroidPreviewScreenshotIdBuilder
            import sergio.sastre.composable.preview.scanner.core.preview.ComposablePreview

            class Dimensions(
                val screenWidthInPx: Int,
                val screenHeightInPx: Int
            )

            object ScreenDimensions {
                fun dimensions(
                    parsedDevice: Device,
                    widthDp: Int,
                    heightDp: Int
                ): Dimensions {
                    val conversionFactor = parsedDevice.densityDpi / 160f
                    val previewWidthInPx = ceil(widthDp * conversionFactor).toInt()
                    val previewHeightInPx = ceil(heightDp * conversionFactor).toInt()
                    return Dimensions(
                        screenHeightInPx = when (heightDp > 0) {
                            true -> previewHeightInPx
                            false -> parsedDevice.dimensions.height.toInt()
                        },
                        screenWidthInPx = when (widthDp > 0) {
                            true -> previewWidthInPx
                            false -> parsedDevice.dimensions.width.toInt()
                        }
                    )
                }
            }

            object DeviceConfigBuilder {
                fun build(preview: AndroidPreviewInfo): DeviceConfig {
                    val parsedDevice =
                        DevicePreviewInfoParser.parse(preview.device)?.inPx() ?: return DeviceConfig()

                    val dimensions = ScreenDimensions.dimensions(
                        parsedDevice = parsedDevice,
                        widthDp = preview.widthDp,
                        heightDp = preview.heightDp
                    )

                    return DeviceConfig(
                        screenHeight = dimensions.screenHeightInPx,
                        screenWidth = dimensions.screenWidthInPx,
                        density = Density(parsedDevice.densityDpi),
                        xdpi = parsedDevice.densityDpi,
                        ydpi = parsedDevice.densityDpi,
                        size = ScreenSize.valueOf(parsedDevice.screenSize.name),
                        ratio = ScreenRatio.valueOf(parsedDevice.screenRatio.name),
                        screenRound = ScreenRound.valueOf(parsedDevice.shape.name),
                        orientation = ScreenOrientation.valueOf(parsedDevice.orientation.name),
                        locale = preview.locale.ifBlank { "en" },
                        fontScale = preview.fontScale,
                        nightMode = when (preview.uiMode and UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES) {
                            true -> NightMode.NIGHT
                            false -> NightMode.NOTNIGHT
                        }
                    )
                }
            }

            private val paparazziTestName =
                TestName(packageName = "Paparazzi", className = "Preview", methodName = "Test")

            private class PreviewSnapshotVerifier(
                maxPercentDifference: Double
            ): SnapshotHandler {
                private val snapshotHandler = SnapshotVerifier(
                    maxPercentDifference = maxPercentDifference
                )
                override fun newFrameHandler(
                    snapshot: Snapshot,
                    frameCount: Int,
                    fps: Int
                ): SnapshotHandler.FrameHandler {
                    val newSnapshot = Snapshot(
                        name = snapshot.name,
                        testName = paparazziTestName,
                        timestamp = snapshot.timestamp,
                        tags = snapshot.tags,
                        file = snapshot.file,
                    )
                    return snapshotHandler.newFrameHandler(
                        snapshot = newSnapshot,
                        frameCount = frameCount,
                        fps = fps
                    )
                }

                override fun close() {
                    snapshotHandler.close()
                }
            }

            private class PreviewHtmlReportWriter: SnapshotHandler {
                private val snapshotHandler = HtmlReportWriter()
                override fun newFrameHandler(
                    snapshot: Snapshot,
                    frameCount: Int,
                    fps: Int
                ): SnapshotHandler.FrameHandler {
                    val newSnapshot = Snapshot(
                        name = snapshot.name,
                        testName = paparazziTestName,
                        timestamp = snapshot.timestamp,
                        tags = snapshot.tags,
                        file = snapshot.file,
                    )
                    return snapshotHandler.newFrameHandler(
                        snapshot = newSnapshot,
                        frameCount = frameCount,
                        fps = fps
                    )
                }

                override fun close() {
                    snapshotHandler.close()
                }
            }

            object PaparazziPreviewRule {
                const val UNDEFINED_API_LEVEL = -1
                const val MAX_API_LEVEL = 36

                fun createFor(preview: ComposablePreview<AndroidPreviewInfo>): Paparazzi {
                    val previewInfo = preview.previewInfo
                    val previewApiLevel = when(previewInfo.apiLevel == UNDEFINED_API_LEVEL) {
                        true -> MAX_API_LEVEL
                        false -> previewInfo.apiLevel
                    }
                    val tolerance = 0.0
                    return Paparazzi(
                        environment = detectEnvironment().copy(compileSdkVersion = previewApiLevel),
                        deviceConfig = DeviceConfigBuilder.build(preview.previewInfo),
                        supportsRtl = true,
                        showSystemUi = previewInfo.showSystemUi,
                        renderingMode = when {
                            previewInfo.showSystemUi -> SessionParams.RenderingMode.NORMAL
                            previewInfo.widthDp > 0 && previewInfo.heightDp > 0 -> SessionParams.RenderingMode.FULL_EXPAND
                            else -> SessionParams.RenderingMode.SHRINK
                        },
                        snapshotHandler = when(System.getProperty("paparazzi.test.verify")?.toBoolean() == true) {
                            true -> PreviewSnapshotVerifier(tolerance)
                            false -> PreviewHtmlReportWriter()
                        },
                        maxPercentDifference = tolerance
                    )
                }
            }

            @Composable
            fun SystemUiSize(
                widthInDp: Int,
                heightInDp: Int,
                content: @Composable () -> Unit
            ) {
                Box(Modifier
                    .size(
                        width = widthInDp.dp,
                        height = heightInDp.dp
                    )
                    .background(Color.White)
                ) {
                    content()
                }
            }

            @Composable
            fun PreviewBackground(
                showBackground: Boolean,
                backgroundColor: Long,
                content: @Composable () -> Unit
            ) {
                when (showBackground) {
                    false -> content()
                    true -> {
                        val color = when (backgroundColor != 0L) {
                            true -> Color(backgroundColor)
                            false -> Color.White
                        }
                        Box(Modifier.background(color)) {
                            content()
                        }
                    }
                }
            }

            /**
             * Auto-generated by Paparazzi Preview Scanner Plugin.
             * Scans packages: ${scanPackages.joinToString(", ")}
             */
            @RunWith(Parameterized::class)
            class $className(
                val preview: ComposablePreview<AndroidPreviewInfo>,
            ) {

                companion object {
                    private val SOURCE_DIRS = listOf($sourceDirsString)

                    private val cachedPreviews: List<ComposablePreview<AndroidPreviewInfo>> by lazy {
                        val allPreviews = AndroidComposablePreviewScanner()
                            .scanPackageTrees($packagesString)
                            ${if (includePrivatePreviews) ".includePrivatePreviews()" else ""}
                            .getPreviews()

                        // Filter to only include previews from the current module by checking
                        // if the source file exists in any of this module's source directories
                        allPreviews.filter { preview ->
                            // Convert class name to file path: com.example.MyClassKt -> com/example/MyClass.kt
                            // Kotlin adds "Kt" suffix to file-level functions, so we need to strip it
                            val className = preview.declaringClass
                            val classPath = if (className.endsWith("Kt")) {
                                className.removeSuffix("Kt").replace('.', '/')
                            } else {
                                className.replace('.', '/')
                            } + ".kt"

                            // Check if the source file exists in any of the module's source directories
                            SOURCE_DIRS.any { sourceDir ->
                                java.io.File(sourceDir, classPath).exists()
                            }
                        }
                    }

                    @JvmStatic
                    @Parameterized.Parameters
                    fun values(): List<ComposablePreview<AndroidPreviewInfo>> = cachedPreviews
                }

                @get:Rule
                val paparazzi: Paparazzi = PaparazziPreviewRule.createFor(preview)

                @Test
                fun snapshot() {
                    val screenshotId = AndroidPreviewScreenshotIdBuilder(preview)
                        .doNotIgnoreMethodParametersType()
                        .encodeUnsafeCharacters()
                        .build()

                    paparazzi.snapshot(name = screenshotId) {
                        val previewInfo = preview.previewInfo
                        when (previewInfo.showSystemUi) {
                            false -> PreviewBackground(
                                showBackground = previewInfo.showBackground,
                                backgroundColor = previewInfo.backgroundColor,
                            ) {
                                preview()
                            }

                            true -> {
                                val parsedDevice = (DevicePreviewInfoParser.parse(previewInfo.device) ?: DEFAULT).inDp()
                                SystemUiSize(
                                    widthInDp = parsedDevice.dimensions.width.toInt(),
                                    heightInDp = parsedDevice.dimensions.height.toInt()
                                ) {
                                    PreviewBackground(
                                        showBackground = true,
                                        backgroundColor = previewInfo.backgroundColor,
                                    ) {
                                        preview()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            """.trimIndent()
    }
}
