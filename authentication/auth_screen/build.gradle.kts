plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.room)
    alias(libs.plugins.ecatalogs.android.library.compose)
}
dependencies {
    implementation(projects.data.uiDesign)
    implementation(projects.authentication.authData)
    implementation(projects.settings.dataSettings.data)
    implementation(projects.data.uiComponents.auth)
    implementation(projects.data.uiComponents.common)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)

    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.material.icons)
}

android {
    buildFeatures {
        compose = true
        viewBinding = true
    }
    namespace = "com.rick.ecatalogs.authentication.auth_screen"
}
