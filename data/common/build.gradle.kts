plugins {
//    alias(libs.plugins.ecatalogs.jvm.library)
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.hilt)
}

dependencies {
    implementation(libs.square.converter.gson)
}
//
android {
    namespace = "com.rick.data.common"
}