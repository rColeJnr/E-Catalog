plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.library.compose)
}

android {
    namespace = "com.rick.data.ui_components.anime_favorite"
}

dependencies {
    api(projects.anime.dataAnime.model)
    api(projects.data.uiComponents.common)
    api(projects.data.uiDesign)

    implementation(libs.compose.coil)
    implementation(libs.lifecycle.runtimeCompose)
}