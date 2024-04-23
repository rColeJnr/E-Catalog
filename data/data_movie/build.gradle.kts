plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.hilt)
    alias(libs.plugins.ecatalogs.android.room)
}

android {
    namespace = "com.rick.data.data_movie"
}

dependencies {
    implementation(projects.data.datastore)
    implementation(projects.data.modelMovie)
    implementation(projects.data.databaseMovie)
    implementation(projects.data.networkMovie)

    implementation(libs.paging.common)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.core)
}