plugins {
    alias(libs.plugins.ecatalogs.android.feature)
}


dependencies {
    implementation(projects.movie.dataMovie.data)
    implementation(projects.movie.dataMovie.model)
    implementation(projects.movie.screenMovie.common)
    implementation(projects.data.uiComponents.movieFavorite)
    implementation(projects.data.analytics)

    implementation(libs.compose.ui)
    implementation(libs.lifecycle.runtimeCompose)
}

android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    namespace = "com.rick.movie.screen_movie.article_favorite"
}