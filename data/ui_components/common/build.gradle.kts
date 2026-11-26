plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.library.compose)
}

android {
    namespace = "com.rick.ecatalogs.data.ui_components.common"
}

dependencies {
    lintPublish(projects.lint)
    implementation(projects.data.uiDesign)
    api(projects.data.translation)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.compose.coil)
    implementation(libs.bumptech.glide)
    implementation(libs.androidx.swiperefreshlayout)
}