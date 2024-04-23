plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.library.compose)
}

android {
    namespace = "com.rick.ui_components.book_favorite"
}

dependencies {
    api(projects.data.modelBook)
    api(projects.uiComponents.common)
    api(libs.lifecycle.runtimeCompose)
}