plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.paging)
//    alias(libs.plugins.ecatalogs.android.room)
}

android {
    namespace = "com.rick.movie.screen_movie.common"
}

dependencies {
    implementation(projects.movie.dataMovie.model)
    implementation(projects.data.analytics)
}