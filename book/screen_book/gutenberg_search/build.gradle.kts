plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.library.compose)
}

dependencies {
    implementation(projects.book.screenBook.common)
    implementation(projects.book.dataBook.data)
    implementation(projects.book.dataBook.model)
    implementation(projects.book.dataBook.domain)
    implementation(projects.data.uiComponents.common)
    implementation(projects.data.uiDesign)
    implementation(projects.data.analytics)

    implementation(libs.compose.ui)
}


android {
    buildFeatures {
        compose = true
    }
    namespace = "com.rick.ecatalogs.book.screen_book.gutenberg_search"
}
