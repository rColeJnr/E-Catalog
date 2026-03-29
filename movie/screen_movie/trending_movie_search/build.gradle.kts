plugins {
    alias(libs.plugins.ecatalogs.android.feature)
}

dependencies {
    implementation(projects.movie.screenMovie.common)
    implementation(projects.movie.dataMovie.data)
    implementation(projects.movie.dataMovie.model)
    implementation(projects.movie.dataMovie.domain)
    implementation(projects.movie.screenMovie.trendingMovieDetails)
    implementation(projects.data.uiComponents.common)
    implementation(projects.data.analytics)
    implementation(projects.data.uiDesign)

    implementation(libs.compose.ui)
}


android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    namespace = "com.rick.movie.screen_movie.trending_movie_search"
}