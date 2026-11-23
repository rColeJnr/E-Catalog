plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
//    alias(libs.plugins.ecatalogs.android.room)
    alias(libs.plugins.ecatalogs.android.library.compose)
}
//
dependencies {
    implementation(projects.anime.dataAnime.data)
    implementation(projects.anime.dataAnime.model)
    implementation(projects.anime.screenAnime.mangaDetails)
    implementation(projects.anime.screenAnime.common)
    implementation(projects.data.uiComponents.animeFavorite)
    implementation(projects.data.analytics)

    implementation(libs.compose.ui)
    implementation(libs.lifecycle.runtimeCompose)
}

android {
    buildFeatures {
        compose = true
    }
    namespace = "com.rick.ecatalogs.anime.screen_anime.manga_favorites"
}
