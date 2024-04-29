plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.hilt)
    alias(libs.plugins.ecatalogs.android.room)
}

android {
    namespace = "com.rick.movie.data_movie.data"
}

dependencies {
    implementation(projects.data.datastore)
    implementation(projects.movie.dataMovie.model)
    implementation(projects.movie.dataMovie.database)
    implementation(projects.movie.dataMovie.network)

    implementation(libs.kotlinx.datetime)
    implementation(libs.paging.common)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.core)
}