plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.paging)
    alias(libs.plugins.ecatalogs.android.library.compose)
}

android {
    buildFeatures {
        compose = true
    }
    namespace = "com.rick.ecatalogs.book.screen_book.bestseller_catalog"
}

dependencies {
    implementation(projects.book.dataBook.data)
    implementation(projects.book.dataBook.domain)
    implementation(projects.book.dataBook.model)
    implementation(projects.book.screenBook.bestsellerFavorites)
    implementation(projects.book.screenBook.bestsellerSearch)
    implementation(projects.book.screenBook.common)
    implementation(projects.data.uiComponents.common)
    implementation(projects.data.uiDesign)
    implementation(projects.data.analytics)

    implementation(libs.compose.ui)
}