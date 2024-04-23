plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.library.compose)
}

android {
    namespace = "com.rick.ui_components.anime_favorite"
}

dependencies {
    implementation(projects.data.modelAnime)
    implementation(projects.uiComponents.common)

    implementation(libs.compose.coil)
    implementation(libs.lifecycle.runtimeCompose)
}