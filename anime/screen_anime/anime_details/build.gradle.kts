plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.library.compose)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
}
//
dependencies {
    implementation(libs.lifecycle.runtimeCompose)
    implementation(projects.anime.screenAnime.common)
    implementation(projects.data.uiComponents.common)
    implementation(projects.data.uiDesign)
    implementation(projects.anime.dataAnime.model)
    implementation(projects.anime.dataAnime.domain)
    implementation(projects.data.analytics)
}

android {
    namespace = "com.rick.ecatalogs.anime.screen_anime.anime_details"
}
