plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.library.compose)
}

android {
    namespace = "com.rick.data.ui_components.book_favorite"
}

dependencies {
    api(projects.book.dataBook.model)
    api(projects.data.uiComponents.common)
    api(libs.lifecycle.runtimeCompose)
}