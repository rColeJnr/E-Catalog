plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.library.compose)
    alias(libs.plugins.ecatalogs.android.paging)
    alias(libs.plugins.ecatalogs.android.hilt)
}

android {
    namespace = "com.rick.ecatalogs.book.screen_book.common"
}

dependencies {
    api(projects.data.uiComponents.common)
    implementation(projects.movie.dataMovie.domain)
    implementation(libs.lifecycle.runtimeCompose)
    implementation(projects.book.dataBook.domain)
    implementation(projects.data.analytics)
    implementation(projects.data.uiDesign)
    implementation(projects.data.translation)

    testImplementation(libs.robolectric)
}
