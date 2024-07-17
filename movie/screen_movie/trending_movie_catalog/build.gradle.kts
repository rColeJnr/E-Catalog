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
    namespace = "com.rick.movie.screen_movie.trending_movie_catalog"
}

dependencies {
    implementation(projects.movie.dataMovie.data)
    implementation(projects.movie.dataMovie.domain)
    implementation(projects.movie.dataMovie.model)
    implementation(projects.movie.screenMovie.common)
    implementation(projects.movie.screenMovie.trendingMovieSearch)
    implementation(projects.movie.screenMovie.trendingMovieDetails)
    implementation(projects.movie.screenMovie.trendingMovieFavorite)
    implementation(projects.data.analytics)
}