plugins {
    alias(libs.plugins.ecatalogs.android.feature)
//    alias(libs.plugins.ecatalogs.android.paging)
//    alias(libs.plugins.ecatalogs.android.room)
}

dependencies {
    implementation(projects.book.screenBook.common)
    implementation(projects.book.dataBook.data)
    implementation(projects.book.dataBook.model)
    implementation(projects.book.dataBook.domain)
    implementation(projects.data.uiComponents.common)

    implementation(libs.compose.ui)
}


android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    namespace = "com.rick.book.screen_book.gutenberg_search"
}
