import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.rick.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.navigation.safeargs.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplicationCompose") {
            id = "ecatalogs.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplication") {
            id = "ecatalogs.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationJacoco") {
            id = "ecatalogs.android.application.jacoco"
            implementationClass = "AndroidApplicationJacocoConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "ecatalogs.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "ecatalogs.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "ecatalogs.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidFeatureData") {
            id = "ecatalogs.android.feature.data"
            implementationClass = "AndroidFeatureDataConventionPlugin"
        }
        register("androidLibraryJacoco") {
            id = "ecatalogs.android.library.jacoco"
            implementationClass = "AndroidLibraryJacocoConventionPlugin"
        }
        register("androidTest") {
            id = "ecatalogs.android.test"
            implementationClass = "AndroidTestConventionPlugin"
        }
        register("androidHilt") {
            id = "ecatalogs.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = "ecatalogs.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("androidNavigation") {
            id = "ecatalogs.android.navigation"
            implementationClass = "AndroidNavigationConventionPlugin"
        }
        register("androidPaging") {
            id = "ecatalogs.android.paging"
            implementationClass = "AndroidPagingConventionPlugin"
        }
        register("androidroidFlavors") {
            id = "ecatalogs.android.application.flavors"
            implementationClass = "AndroidApplicationFlavorsConventionPlugin"
        }
        register("androidLint") {
            id = "ecatalogs.android.lint"
            implementationClass = "AndroidLintConventionPlugin"
        }
        register("jvmLibrary") {
            id = "ecatalogs.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}
