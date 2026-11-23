plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.hilt)
}

android {
    namespace = "com.rick.ecatalogs.data.sync"
}

dependencies {
    ksp(libs.hilt.ext.compiler)

    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)

    implementation(projects.data.analytics)
    implementation(projects.data.common)
    implementation(projects.movie.dataMovie.data)

    api(projects.data.util)
}
