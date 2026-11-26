import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.hilt)
}

android {
    namespace = "com.rick.ecatalogs.game.data_game.network"
    buildFeatures {
        buildConfig = true
    }
    // IGDB (Twitch) app credentials from local.properties: igdbClientId, igdbClientSecret.
    // Quotes written in local.properties are tolerated, like translationApiKey.
    val localProperties = gradleLocalProperties(rootDir, providers)
    fun property(name: String) = localProperties.getProperty(name, "").trim().removeSurrounding("\"")
    defaultConfig {
        buildConfigField("String", "IGDB_CLIENT_ID", "\"${property("igdbClientId")}\"")
        buildConfigField("String", "IGDB_CLIENT_SECRET", "\"${property("igdbClientSecret")}\"")
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    api(projects.game.dataGame.model)
    implementation(projects.data.common) // KeystoreCipher for the stored Twitch token
    api(libs.square.retrofit)
    api(libs.square.okhttp)
    api(libs.square.logging.interceptor)
    api(libs.square.converter.gson)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
}
