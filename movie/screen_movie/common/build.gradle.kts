plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.paging)
    alias(libs.plugins.ecatalogs.android.hilt)
}

android {
    namespace = "com.rick.movie.screen_movie.common"
}

dependencies {
    implementation(projects.movie.dataMovie.model)
    implementation(projects.movie.dataMovie.domain)
    implementation(projects.data.analytics)
    implementation(projects.data.translation)
}