plugins {
    alias(libs.plugins.ecatalogs.jvm.library)
    id("kotlinx-serialization")
}


dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.square.converter.gson)
    implementation(libs.kotlinx.serialization.json)
}