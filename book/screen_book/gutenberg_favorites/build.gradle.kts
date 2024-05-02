plugins {
    alias(libs.plugins.ecatalogs.android.feature)
//    alias(libs.plugins.ecatalogs.android.paging)
//    alias(libs.plugins.ecatalogs.android.room)
}

android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    namespace = "com.rick.book.screen_book.gutenberg_favorites"
}

dependencies {
    implementation(projects.book.dataBook.data)
    implementation(projects.book.screenBook.common)
    implementation(projects.book.dataBook.model)
    implementation(projects.data.uiComponents.bookFavorite)
    implementation(projects.data.analytics)

    implementation(libs.compose.ui)
    implementation(libs.lifecycle.runtimeCompose)
}