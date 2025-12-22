package com.emergetools.paparazzi

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.HasUnitTest
import com.emergetools.paparazzi.tasks.GeneratePreviewScannerTestTask
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject

abstract class PaparazziPreviewScannerPlugin @Inject constructor() : Plugin<Project> {

    override fun apply(project: Project) {
        // Create extension for user configuration
        val extension = project.extensions.create(
            "paparazziPreviewScanner",
            PaparazziPreviewScannerExtension::class.java,
            project
        )

        // Wait for Android application plugin to be applied
        project.pluginManager.withPlugin("com.android.application") {
            configureForAndroid(project, extension)
        }

        // Also support Android library modules
        project.pluginManager.withPlugin("com.android.library") {
            configureForAndroid(project, extension)
        }
    }

    private fun configureForAndroid(project: Project, extension: PaparazziPreviewScannerExtension) {
        // Check if plugin is enabled
        if (!extension.enabled.get()) {
            project.logger.debug("Paparazzi Preview Scanner plugin is disabled")
            return
        }

        // This only works if the paparazzi plugin is already on the classpath.

        if (!project.pluginManager.hasPlugin("app.cash.paparazzi")) {
            project.logger.debug("Applying Paparazzi plugin")
            project.pluginManager.apply("app.cash.paparazzi")
        }

        // Get Android Components extension
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

        // Configure for each variant
        androidComponents.onVariants { variant ->
            // Only generate for variants that have unit tests
            if (variant is HasUnitTest) {
              println("variant has unit tests")
                val unitTest = variant.unitTest ?: return@onVariants

                // Register test generation task
                val generateTask = registerGenerateTask(project, variant.name, variant.namespace, extension)
              println("generateTask registered for variant ${variant.name} and ${variant.namespace}")


                // Wire generated sources to test source set
                unitTest.sources.java!!.addGeneratedSourceDirectory(
                    generateTask,
                    GeneratePreviewScannerTestTask::outputDirectory
                )

                project.logger.lifecycle(
                    "Paparazzi Preview Scanner: Registered test generation for variant '${variant.name}'"
                )
            }
        }

        // Auto-inject dependencies
        injectDependencies(project, extension)
    }

    private fun registerGenerateTask(
        project: Project,
        variantName: String,
        namespace: org.gradle.api.provider.Provider<String>,
        extension: PaparazziPreviewScannerExtension
    ): TaskProvider<GeneratePreviewScannerTestTask> {
        val taskName = "generate${variantName.replaceFirstChar { it.titlecase() }}PaparazziPreviewScannerTest"

        val taskProvider = project.tasks.register(taskName, GeneratePreviewScannerTestTask::class.java)

        taskProvider.configure(object : Action<GeneratePreviewScannerTestTask> {
            override fun execute(task: GeneratePreviewScannerTestTask) {
                task.scanPackages.set(extension.scanPackages)
                task.namespace.set(namespace)
                task.includePrivatePreviews.set(extension.includePrivatePreviews)

                task.outputDirectory.set(
                    project.layout.buildDirectory.dir(
                        "generated/source/paparazzi/${variantName}"
                    )
                )
                task.logger.lifecycle("Output directory is ${task.outputDirectory.get()}")
            }
        })

        return taskProvider
    }

    private fun injectDependencies(project: Project, extension: PaparazziPreviewScannerExtension) {
        project.dependencies.apply {
            val previewScannerVersion = extension.previewScannerVersion.get()

            add(
                "testImplementation",
                "io.github.sergio-sastre.ComposablePreviewScanner:android:$previewScannerVersion"
            )

            project.logger.lifecycle(
                "Paparazzi Preview Scanner: Auto-injected dependencies " +
                "(preview-scanner: $previewScannerVersion)"
            )
        }
    }
}
