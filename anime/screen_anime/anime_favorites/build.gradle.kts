plugins {
    alias(libs.plugins.ecatalogs.android.feature)
//    alias(libs.plugins.ecatalogs.android.paging)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
//    alias(libs.plugins.ecatalogs.android.room)
}
//
dependencies {
    implementation(projects.anime.dataAnime.data)
    implementation(projects.anime.dataAnime.model)
    implementation(projects.anime.screenAnime.animeDetails)
    implementation(projects.anime.screenAnime.common)
    implementation(projects.data.uiComponents.animeFavorite)
    implementation(projects.data.analytics)
//
    implementation(libs.compose.ui)
    implementation(libs.lifecycle.runtimeCompose)
}

android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    namespace = "com.rick.anime.screen_anime.anime_favorites"
}
