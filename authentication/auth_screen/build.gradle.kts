plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    id("com.google.gms.google-services")
    alias(libs.plugins.ecatalogs.android.room)
}
dependencies {
    implementation(projects.authentication.authData)
    implementation(projects.settings.dataSettings.data)
    implementation(projects.data.uiComponents.auth)

    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.material.icons)
}

android {
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    namespace = "com.rick.auth_screen"
}
