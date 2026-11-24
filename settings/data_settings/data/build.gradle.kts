plugins {
    alias(libs.plugins.ecatalogs.android.feature.data)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
}

android {
    namespace = "com.rick.ecatalogs.settings.data_settings.data"
}

dependencies {
//    implementation(libs.androidx.appcompat)
//    implementation(libs.google.oss.licenses)
    implementation(projects.data.datastore)
    implementation(projects.data.analytics)
    implementation(projects.settings.dataSettings.model)

    testImplementation(projects.data.datastoreTest)
    testImplementation(projects.data.testing)
}
