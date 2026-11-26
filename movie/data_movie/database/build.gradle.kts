plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.hilt)
    alias(libs.plugins.ecatalogs.android.room)
}

android {
    namespace = "com.rick.ecatalogs.movie.data_movie.database"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    api(projects.movie.dataMovie.model)
    api(projects.data.common)

    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.paging.common)
    implementation(libs.room.paging)
    implementation(libs.square.converter.gson)

    // DAO tests on an in-memory Room database (Robolectric)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.kotlinx.coroutines.test)
}