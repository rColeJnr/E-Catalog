plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.rick.anime.data_anime.domain"
}

dependencies {
    api(projects.anime.dataAnime.data)
    api(projects.anime.dataAnime.model)
    api(projects.data.translation)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

//    testImplementation(projects.core.testing)
}