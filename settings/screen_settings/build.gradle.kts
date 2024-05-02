plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.library.compose)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
}

android {
    namespace = "com.rick.settings.screen_settings"
}

dependencies {
    implementation(libs.google.oss.licenses)
    implementation(libs.lifecycle.runtimeCompose)
    implementation(projects.settings.dataSettings.data)
    implementation(projects.settings.dataSettings.model)

}
