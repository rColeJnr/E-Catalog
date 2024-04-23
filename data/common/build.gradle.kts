plugins {
//    alias(libs.plugins.ecatalogs.jvm.library)
    alias(libs.plugins.ecatalogs.android.application)
    alias(libs.plugins.ecatalogs.android.application.jacoco)
}

dependencies {
    implementation(libs.square.converter.gson)
}
//
android {
    namespace = "com.rick.data.common"
}