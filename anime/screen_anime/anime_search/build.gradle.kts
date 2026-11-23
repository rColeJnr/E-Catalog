plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.library.compose)
}
//
dependencies {
    implementation(projects.anime.screenAnime.animeDetails)
    implementation(projects.anime.screenAnime.common)
    implementation(projects.anime.dataAnime.data)
    implementation(projects.anime.dataAnime.domain)
    implementation(projects.anime.dataAnime.model)
    implementation(projects.data.uiComponents.common)
    implementation(projects.data.analytics)
    implementation(projects.data.uiDesign)

    implementation(libs.compose.ui)
}

android {
    buildFeatures {
        compose = true
    }
    namespace = "com.rick.ecatalogs.anime.screen_anime.anime_search"
}
