plugins {
    alias(libs.plugins.ecatalogs.android.feature)
//    alias(libs.plugins.ecatalogs.android.paging)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
//    alias(libs.plugins.ecatalogs.android.room)
}
//
dependencies {
    implementation(projects.anime.screenAnime.common)
    implementation(projects.data.uiComponents.common)
    implementation(projects.anime.dataAnime.model)
    implementation(projects.anime.dataAnime.domain)
    implementation(projects.data.analytics)
}

android {
    namespace = "com.rick.anime.screen_anime.anime_details"
}
