plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.library.compose)
}

android {
    namespace = "com.rick.data.ui_components.common"
}

dependencies {
    implementation(libs.compose.coil)
    implementation(libs.bumptech.glide)
}