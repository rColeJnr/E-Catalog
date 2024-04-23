plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.library.compose)
}

android {
    namespace = "com.rick.ui_components.movie_favorite"
}

dependencies {
    api(projects.data.modelMovie)
    api(projects.uiComponents.common)
    api(libs.lifecycle.runtimeCompose)
}