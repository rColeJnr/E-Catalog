plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.compose)
    alias(libs.plugins.ecatalogs.android.hilt)
}

android {
    namespace = "com.rick.data.analytics"
}

dependencies {
    implementation(libs.compose.runtime)

    prodImplementation(platform(libs.firebase.bom))
    prodImplementation(libs.firebase.analytics)
}
