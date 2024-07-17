import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.hilt)
    id("kotlinx-serialization")
}

android {
    buildFeatures {
        buildConfig = true
    }
    namespace = "com.rick.data.translation"

    val key: String = gradleLocalProperties(rootDir, providers).getProperty("translationApiKey")
    defaultConfig {
        buildConfigField(
            "String", "translationApiKey",
            key
        )
    }
}

dependencies {
    api(libs.square.retrofit)
    api(libs.square.okhttp)
    api(libs.square.logging.interceptor)
    api(libs.square.converter.gson)
    api(libs.kotlinx.serialization.json)
}