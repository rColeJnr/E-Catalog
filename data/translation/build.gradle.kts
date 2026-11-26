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
    namespace = "com.rick.ecatalogs.data.translation"

    // Quoted here like the other API keys, so local.properties holds the plain value (quotes
    // written there too are tolerated)
    val key = gradleLocalProperties(rootDir, providers).getProperty("translationApiKey", "")
        .trim().removeSurrounding("\"")
    defaultConfig {
        buildConfigField("String", "translationApiKey", "\"$key\"")
    }
}

dependencies {
    api(libs.square.retrofit)
    api(libs.square.okhttp)
    api(libs.square.logging.interceptor)
    api(libs.square.converter.gson)
    api(libs.kotlinx.serialization.json)

    testImplementation(libs.kotlinx.coroutines.test)
}