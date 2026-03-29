plugins {
    alias(libs.plugins.ecatalogs.android.feature)
}

android {
    externalNativeBuild {
        ndkBuild {
            path = file("src/main/jni/Android.mk") //Path of android.mk
        }
    }
    namespace = "com.rick.movie.screen_movie.trending_series_details"
}

dependencies {
    implementation(projects.movie.dataMovie.model)
    implementation(projects.movie.dataMovie.domain)
    implementation(projects.movie.screenMovie.common)
    implementation(projects.data.analytics)
    implementation(projects.data.uiDesign)
}