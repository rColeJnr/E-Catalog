plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.gms)
}
dependencies {
    implementation(projects.authentication.authData)
    implementation(projects.uiComponents.auth)

    implementation(libs.firebase.bom)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)

    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
}

android {
    namespace = "com.rick.auth_screen"
}
