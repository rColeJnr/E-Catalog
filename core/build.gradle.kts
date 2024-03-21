plugins {
//    alias(libs.plugins.ecatalogs.jvm.library)
    alias(libs.plugins.ecatalogs.android.application)
}

dependencies {
    implementation(libs.square.converter.gson)
}
//
android {
    namespace = "com.rick.core"
}