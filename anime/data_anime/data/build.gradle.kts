plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.hilt)
    alias(libs.plugins.ecatalogs.android.room)
}

android {
    namespace = "com.rick.ecatalogs.anime.data_anime.data"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(projects.data.datastore)
    implementation(projects.data.analytics)
    implementation(projects.anime.dataAnime.model)
    implementation(projects.anime.dataAnime.database)
    implementation(projects.anime.dataAnime.network)

    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(projects.data.datastoreTest)
    testImplementation(libs.kotlinx.coroutines.test)
    // Data-layer tests: real Room (in memory, on Robolectric) behind fake networks
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.room.runtime)
    testImplementation(libs.square.converter.gson)
    implementation(libs.paging.common)
}