plugins {
    alias(libs.plugins.ecatalogs.android.feature)
}

android {
    namespace = "com.rick.ecatalogs.movie.screen_movie.trending_movie_details"
}

dependencies {
    implementation(projects.movie.dataMovie.model)
    implementation(projects.movie.dataMovie.domain)
    implementation(projects.movie.screenMovie.common)
    implementation(projects.data.analytics)
    implementation(projects.data.uiDesign)
}