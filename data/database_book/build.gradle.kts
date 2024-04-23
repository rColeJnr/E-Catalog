plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.hilt)
    alias(libs.plugins.ecatalogs.android.room)
}

android {
    namespace = "com.rick.data.database_book"
}

dependencies {
    api(projects.data.modelBook)
    api(projects.data.common)
    implementation(libs.square.converter.gson)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.room.paging)
    implementation(libs.paging.common)
}