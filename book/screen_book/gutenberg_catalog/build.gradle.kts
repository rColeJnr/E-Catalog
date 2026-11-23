plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.paging)
    alias(libs.plugins.ecatalogs.android.library.compose)
}

android {
    buildFeatures {
        compose = true
    }
    namespace = "com.rick.ecatalogs.book.screen_book.gutenberg_catalog"
}

dependencies {
    implementation(projects.book.dataBook.data)
    implementation(projects.book.dataBook.domain)
    implementation(projects.book.dataBook.model)
    implementation(projects.book.screenBook.common)
    implementation(projects.book.screenBook.gutenbergSearch)
    implementation(projects.book.screenBook.gutenbergFavorites)
    implementation(projects.data.analytics)
    implementation(projects.data.uiComponents.common)
    implementation(projects.data.uiDesign)

    implementation(libs.compose.ui)
}