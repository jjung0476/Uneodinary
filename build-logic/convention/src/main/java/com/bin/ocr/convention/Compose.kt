package com.bin.ocr.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        compileSdk = 34

        buildFeatures {
            compose = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        dependencies {
            val bom = libs.findLibrary("androidx.compose.bom").get()
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // Configure the Compose Compiler
    extensions.getByType<ComposeCompilerGradlePluginExtension>().apply {
        val enableMetricsProvider = project.providers.gradleProperty("enableComposeCompilerMetrics")
        val enableMetrics = (enableMetricsProvider.orNull == "true")
        if (enableMetrics) {
            metricsDestination.set(layout.buildDirectory.dir("compose-metrics"))
        }
        val enableReportsProvider = project.providers.gradleProperty("enableComposeCompilerReports")
        val enableReports = (enableReportsProvider.orNull == "true")
        if (enableReports) {
            reportsDestination.set(layout.buildDirectory.dir("compose-reports"))
        }
    }
}
