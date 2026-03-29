plugins {
    alias(libs.plugins.ecatalogs.android.feature)
}

android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    namespace = "com.rick.book.screen_book.bestseller_search"
}

dependencies {
    implementation(projects.book.dataBook.data)
    implementation(projects.book.dataBook.model)
    implementation(projects.data.uiComponents.common)
    implementation(projects.data.uiDesign)
    implementation(projects.data.analytics)

    implementation(libs.compose.ui)
}

