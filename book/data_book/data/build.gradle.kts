plugins {
    alias(libs.plugins.ecatalogs.android.feature.data)
}

android {
    namespace = "com.rick.book.data_book.data"
}

dependencies {
    implementation(projects.data.datastore)
    implementation(projects.book.dataBook.model)
    implementation(projects.book.dataBook.database)
    implementation(projects.book.dataBook.network)

    implementation(libs.kotlinx.datetime)
    implementation(libs.paging.common)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.core)
}