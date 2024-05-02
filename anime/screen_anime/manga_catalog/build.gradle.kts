plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.paging)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
}
//
dependencies {
    implementation(projects.anime.dataAnime.data)
    implementation(projects.anime.dataAnime.model)
    implementation(projects.anime.screenAnime.common)
    implementation(projects.anime.screenAnime.mangaDetails)
    implementation(projects.anime.screenAnime.mangaFavorites)
    implementation(projects.anime.screenAnime.mangaSearch)
    implementation(projects.data.uiComponents.common)
    implementation(projects.data.analytics)
}

android {
    namespace = "com.rick.anime.screen_anime.manga_catalog"
}
