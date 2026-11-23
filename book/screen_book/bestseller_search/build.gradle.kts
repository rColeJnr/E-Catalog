plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.library.compose)
}

android {
    buildFeatures {
        compose = true
    }
    namespace = "com.rick.ecatalogs.book.screen_book.bestseller_search"
}

dependencies {
    implementation(projects.book.dataBook.data)
    implementation(projects.book.dataBook.model)
    implementation(projects.data.uiComponents.common)
    implementation(projects.data.uiDesign)
    implementation(projects.data.analytics)

    implementation(libs.compose.ui)
}

