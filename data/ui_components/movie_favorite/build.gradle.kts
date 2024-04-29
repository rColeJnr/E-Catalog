plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.library.compose)
}

android {
    namespace = "com.rick.data.ui_components.movie_favorite"
}

dependencies {
    api(projects.movie.dataMovie.model)
    api(projects.data.uiComponents.common)
    api(libs.lifecycle.runtimeCompose)
}