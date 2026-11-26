package com.rick.ecatalogs.buildlogic

/**
 * Gradle 9 fails a test task that discovers no tests when (generated, e.g. Hilt/KSP) test
 * sources exist. Modules without tests yet shouldn't fail `./gradlew test`.
 */
internal fun org.gradle.api.Project.allowModulesWithoutTests() {
    tasks.withType(org.gradle.api.tasks.testing.Test::class.java).configureEach {
        failOnNoDiscoveredTests.set(false)
    }
}
