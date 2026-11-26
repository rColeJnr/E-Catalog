plugins {
    alias(libs.plugins.ecatalogs.android.feature.data)
}

android {
    namespace = "com.rick.ecatalogs.book.data_book.data"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    implementation(projects.data.datastore)
    implementation(projects.data.analytics)
    implementation(projects.book.dataBook.model)
    implementation(projects.book.dataBook.database)
    implementation(projects.book.dataBook.network)

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
