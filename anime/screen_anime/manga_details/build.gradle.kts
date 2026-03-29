plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
}
//
dependencies {
    implementation(projects.anime.screenAnime.common)
    implementation(projects.data.uiComponents.common)
    implementation(projects.anime.dataAnime.model)
    implementation(projects.anime.dataAnime.domain)
    implementation(projects.data.analytics)
    implementation(projects.data.uiDesign)
}

android {
    namespace = "com.rick.anime.screen_anime.manga_details"
}
