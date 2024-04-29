plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
}
//
dependencies {
    implementation(projects.anime.screenAnime.animeDetails)
    implementation(projects.anime.screenAnime.common)
    implementation(projects.anime.dataAnime.data)
    implementation(projects.anime.dataAnime.domain)
    implementation(projects.anime.dataAnime.model)
    implementation(projects.data.uiComponents.common)

    implementation(libs.compose.ui)
}

android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    namespace = "com.rick.anime.screen_anime.anime_search"
}
