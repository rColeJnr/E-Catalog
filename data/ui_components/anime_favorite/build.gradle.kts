plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.library.compose)
}

android {
    namespace = "com.rick.data.ui_components.anime_favorite"
}

dependencies {
    implementation(projects.anime.dataAnime.model)
    implementation(projects.data.uiComponents.common)

    implementation(libs.compose.coil)
    implementation(libs.lifecycle.runtimeCompose)
}