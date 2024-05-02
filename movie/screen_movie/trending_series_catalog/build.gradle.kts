plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.paging)
//    alias(libs.plugins.ecatalogs.android.room)
}

android {
    externalNativeBuild {
        ndkBuild {
            path = file("src/main/jni/Android.mk") //Path of android.mk
        }
    }
    namespace = "com.rick.movie.screen_movie.trending_series_catalog"
}

dependencies {
    implementation(projects.movie.dataMovie.data)
    implementation(projects.movie.dataMovie.model)
    implementation(projects.movie.screenMovie.common)
    implementation(projects.movie.screenMovie.trendingSeriesSearch)
    implementation(projects.movie.screenMovie.trendingSeriesDetails)
    implementation(projects.movie.screenMovie.trendingSeriesFavorite)
    implementation(projects.data.analytics)
}