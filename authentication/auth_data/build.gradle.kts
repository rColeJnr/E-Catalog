plugins {
    alias(libs.plugins.ecatalogs.android.feature.data)
}

android {
    namespace = "com.rick.ecatalogs.authentication.auth_data"
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
}
