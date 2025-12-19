package com.emergetools.paparazzi

import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

abstract class PaparazziPreviewScannerExtension @Inject constructor(project: Project) {

    private val objects = project.objects

    /**
     * Enable/disable the plugin.
     * Default: true
     */
    val enabled: Property<Boolean> =
        objects.property(Boolean::class.java).convention(true)

    /**
     * Package names to scan for @Preview annotations.
     * Default: auto-detect from android.namespace or applicationId
     */
    val scanPackages: ListProperty<String> =
        objects.listProperty(String::class.java).convention(emptyList())

    /**
     * Paparazzi version to use.
     * Default: "2.0.0-alpha02"
     */
    val paparazziVersion: Property<String> =
        objects.property(String::class.java).convention("2.0.0-alpha02")

    /**
     * ComposePreviewScanner version.
     * Default: "0.7.2"
     */
    val previewScannerVersion: Property<String> =
        objects.property(String::class.java).convention("0.7.2")
}
