plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.hilt)
}

android {
    buildFeatures {
        buildConfig = true
    }
    namespace = "com.rick.data.translation"
    defaultConfig {
        buildConfigField(
            "String", "TranslationApiKey",
            properties["translationApiKey"].toString()
        )
    }
}

dependencies {
    api(libs.square.retrofit)
    api(libs.square.okhttp)
    api(libs.square.logging.interceptor)
    api(libs.square.converter.gson)
}