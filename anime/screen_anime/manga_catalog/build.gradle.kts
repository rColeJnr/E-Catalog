plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.library.compose)
    alias(libs.plugins.ecatalogs.android.paging)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
}
//
dependencies {
    implementation(libs.lifecycle.runtimeCompose)
    implementation(projects.anime.dataAnime.data)
    implementation(projects.anime.dataAnime.domain)
    implementation(projects.anime.dataAnime.model)
    implementation(projects.anime.screenAnime.common)
    implementation(projects.anime.screenAnime.mangaDetails)
    implementation(projects.anime.screenAnime.mangaFavorites)
    implementation(projects.anime.screenAnime.mangaSearch)
    implementation(projects.data.analytics)
    implementation(projects.data.uiComponents.common)
    implementation(projects.data.uiDesign)
}

android {
    namespace = "com.rick.ecatalogs.anime.screen_anime.manga_catalog"
}
