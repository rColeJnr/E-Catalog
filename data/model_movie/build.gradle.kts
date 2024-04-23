plugins {
    alias(libs.plugins.ecatalogs.jvm.library)
    id("kotlinx-serialization")
}


dependencies {
    implementation(libs.square.converter.gson)
    implementation(libs.kotlinx.serialization.json)
}