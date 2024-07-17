plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.rick.movie.data_movie.domain"
}

dependencies {
    api(projects.movie.dataMovie.data)
    api(projects.movie.dataMovie.model)
    api(projects.data.translation)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

//    testImplementation(projects.core.testing)
}