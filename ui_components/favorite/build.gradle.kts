plugins {
    alias(libs.plugins.ecatalogs.android.application.compose)
}

android {
    namespace = "com.rick.ui_components.favorite"
}

dependencies {
    implementation(libs.compose.coil)
}