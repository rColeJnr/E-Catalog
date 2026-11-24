plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.library.compose)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
}

android {
    namespace = "com.rick.ecatalogs.settings.screen_settings"
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.google.oss.licenses)
    implementation(libs.lifecycle.runtimeCompose)
    implementation(projects.settings.dataSettings.data)
    implementation(projects.settings.dataSettings.model)
    implementation(projects.data.uiComponents.common)
    implementation(projects.data.uiDesign)
    implementation(projects.data.util)
    testImplementation(projects.data.testing)
}
