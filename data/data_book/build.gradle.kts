plugins {
    alias(libs.plugins.ecatalogs.android.feature.data)
}

android {
    namespace = "com.rick.data.data_book"
}

dependencies {
    implementation(projects.data.datastore)
    implementation(projects.data.modelBook)
    implementation(projects.data.databaseBook)
    implementation(projects.data.networkBook)

    implementation(libs.paging.common)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.core)
}