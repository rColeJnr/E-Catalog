plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.hilt)
    alias(libs.plugins.ecatalogs.android.room)
}

android {
    namespace = "com.rick.anime.data_anime.database"
}

dependencies {
    api(projects.anime.dataAnime.model)
    api(projects.data.common)

    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.paging.common)
    implementation(libs.room.paging)
    implementation(libs.square.converter.gson)
}