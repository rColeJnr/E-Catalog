plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.library.compose)
}

dependencies {
    implementation(projects.movie.screenMovie.common)
    implementation(projects.movie.screenMovie.trendingSeriesDetails)
    implementation(projects.movie.dataMovie.data)
    implementation(projects.movie.dataMovie.model)
    implementation(projects.movie.dataMovie.domain)
    implementation(projects.data.uiComponents.common)
    implementation(projects.data.analytics)
    implementation(projects.data.uiDesign)

    implementation(libs.compose.ui)
}


android {
    buildFeatures {
        compose = true
    }
    namespace = "com.rick.ecatalogs.movie.screen_movie.trending_series_search"
}