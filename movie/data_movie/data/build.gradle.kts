plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.hilt)
    alias(libs.plugins.ecatalogs.android.room)
}

android {
    namespace = "com.rick.ecatalogs.movie.data_movie.data"
    testOptions {
        unitTests {
            // Repository tests pass a ContextWrapper stub for the cache directory
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(projects.data.datastore)
    implementation(projects.data.util)
    implementation(projects.data.analytics)
    implementation(projects.movie.dataMovie.model)
    implementation(projects.movie.dataMovie.database)
    implementation(projects.movie.dataMovie.network)

    implementation(libs.kotlinx.datetime)
    implementation(libs.paging.common)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(projects.data.datastoreTest)
    testImplementation(libs.kotlinx.coroutines.test)
    // Data-layer tests: real Room (in memory, on Robolectric) behind fake networks
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.room.runtime)
    testImplementation(libs.square.converter.gson)
}