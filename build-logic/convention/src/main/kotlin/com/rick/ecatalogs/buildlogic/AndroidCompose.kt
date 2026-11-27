package com.rick.ecatalogs.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

/**
 * Configure Compose-specific options. The project must also apply the
 * `org.jetbrains.kotlin.plugin.compose` plugin (done by the compose convention plugins).
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension,
) {
    commonExtension.apply {
        buildFeatures.apply {
            compose = true
        }

        testOptions.apply {
            unitTests.apply {
                // For Robolectric
                isIncludeAndroidResources = true
            }
        }
    }

    dependencies {
        val bom = libs.findLibrary("compose-bom").get()
        "implementation"(platform(bom))
        "androidTestImplementation"(platform(bom))
        "implementation"(libs.findLibrary("compose-ui").get())
        "implementation"(libs.findLibrary("compose.runtime").get())
        "implementation"(libs.findLibrary("compose.material").get())
        "implementation"(libs.findLibrary("compose.material.icons").get())
        "implementation"(libs.findLibrary("compose.foundation").get())
        "implementation"(libs.findLibrary("compose.foundation.layout").get())
        "implementation"(libs.findLibrary("compose.constraint.layout").get())
        "implementation"(libs.findLibrary("compose.ui.tooling.preview").get())
        "implementation"(libs.findLibrary("compose-ui-tooling").get())
    }

    extensions.configure<ComposeCompilerGradlePluginExtension> {
        fun Provider<String>.onlyIfTrue() = flatMap { provider { it.takeIf(String::toBoolean) } }
        fun Provider<*>.relativeToRootProject(dir: String) = map {
            rootProject.layout.projectDirectory
                .dir("build")
                .dir(projectDir.toRelativeString(rootDir))
        }.map { it.dir(dir) }

        project.providers.gradleProperty("enableComposeCompilerMetrics").onlyIfTrue()
            .relativeToRootProject("compose-metrics")
            .let(metricsDestination::set)

        project.providers.gradleProperty("enableComposeCompilerReports").onlyIfTrue()
            .relativeToRootProject("compose-reports")
            .let(reportsDestination::set)

        val stabilityConfig = rootProject.layout.projectDirectory.file("compose_compiler_config.conf")
        if (stabilityConfig.asFile.exists()) {
            stabilityConfigurationFiles.add(stabilityConfig)
        }
    }
}
