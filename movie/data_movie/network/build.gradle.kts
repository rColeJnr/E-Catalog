import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.hilt)
    alias(libs.plugins.ecatalogs.android.room)
    id("kotlinx-serialization")
}

android {
    namespace = "com.rick.ecatalogs.movie.data_movie.network"
    buildFeatures {
        buildConfig = true
    }
    // API keys live in local.properties (git-ignored): tmdbApiKey=... and nyTimesApiKey=...
    val localProperties = gradleLocalProperties(rootDir, providers)
    defaultConfig {
        buildConfigField(
            "String", "TMDB_API_KEY",
            "\"${localProperties.getProperty("tmdbApiKey", "")}\""
        )
        buildConfigField(
            "String", "NY_TIMES_API_KEY",
            "\"${localProperties.getProperty("nyTimesApiKey", "")}\""
        )
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    api(projects.movie.dataMovie.model)

    api(libs.square.retrofit)
    api(libs.square.okhttp)
    api(libs.square.logging.interceptor)
    api(libs.square.converter.gson)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
}