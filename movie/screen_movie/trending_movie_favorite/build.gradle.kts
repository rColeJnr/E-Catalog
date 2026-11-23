plugins {
    alias(libs.plugins.ecatalogs.android.feature)
//    alias(libs.plugins.ecatalogs.android.paging)
//    alias(libs.plugins.ecatalogs.android.room)
    alias(libs.plugins.ecatalogs.android.library.compose)
}

dependencies {
    implementation(projects.data.uiDesign)
    implementation(projects.movie.dataMovie.data)
    implementation(projects.movie.dataMovie.model)
    implementation(projects.movie.screenMovie.common)
    implementation(projects.movie.screenMovie.trendingMovieDetails)
    implementation(projects.data.uiComponents.movieFavorite)
    implementation(projects.data.analytics)

    implementation(libs.compose.ui)
    implementation(libs.lifecycle.runtimeCompose)
}

android {
    buildFeatures {
        compose = true
    }
    namespace = "com.rick.ecatalogs.movie.screen_movie.trending_movie_favorite"
}