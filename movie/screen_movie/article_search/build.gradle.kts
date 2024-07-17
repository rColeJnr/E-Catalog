plugins {
    alias(libs.plugins.ecatalogs.android.feature)
}

dependencies {
    implementation(projects.movie.screenMovie.common)
    implementation(projects.movie.dataMovie.data)
    implementation(projects.movie.dataMovie.model)
    implementation(projects.movie.dataMovie.domain)
    implementation(projects.data.uiComponents.common)
    implementation(projects.data.analytics)

    implementation(libs.compose.ui)
}


android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    namespace = "com.rick.movie.screen_movie.article_search"
}