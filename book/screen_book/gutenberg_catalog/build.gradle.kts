plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.paging)
//    alias(libs.plugins.ecatalogs.android.room)
}

android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    namespace = "com.rick.book.screen_book.gutenberg_catalog"
}

dependencies {
    implementation(projects.book.dataBook.data)
    implementation(projects.book.dataBook.model)
    implementation(projects.book.screenBook.common)
    implementation(projects.book.screenBook.gutenbergSearch)
    implementation(projects.book.screenBook.gutenbergFavorites)
    implementation(projects.data.uiComponents.common)
    implementation(projects.data.analytics)

    implementation(libs.compose.ui)
}