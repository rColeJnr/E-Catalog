plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.paging)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.room)
}
//
dependencies {
//    implementation(projects.data.common)
    implementation(projects.data.modelAnime)
    implementation(projects.data.dataAnime)
    implementation(projects.uiComponents.animeFavorite)

    implementation(libs.compose.ui)
    implementation(libs.lifecycle.runtimeCompose)
}

android {
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    externalNativeBuild {
        ndkBuild {
            path = file("src/main/jni/Android.mk") //Path of android.mk
        }
    }
    namespace = "com.rick.screen_anime"
}
