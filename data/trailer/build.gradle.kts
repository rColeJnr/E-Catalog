plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.compose)
}

android {
    namespace = "com.rick.ecatalogs.data.trailer"
}

dependencies {
    // EcsTheme for the trailer screen
    implementation(projects.data.uiComponents.common)
    implementation(projects.data.uiDesign)
    implementation(libs.youtube.player.core)
    implementation(libs.compose.coil)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.core)
    implementation(libs.lifecycle.runtimeCompose)

    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
}
