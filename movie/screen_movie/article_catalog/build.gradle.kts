plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.paging)
}

android {
    externalNativeBuild {
        ndkBuild {
            path = file("src/main/jni/Android.mk") //Path of android.mk
        }
    }
    namespace = "com.rick.movie.screen_movie.article_catalog"
}

dependencies {
    implementation(projects.movie.dataMovie.data)
    implementation(projects.movie.dataMovie.domain)
    implementation(projects.movie.dataMovie.model)
    implementation(projects.movie.screenMovie.common)
    implementation(projects.movie.screenMovie.articleFavorite)
    implementation(projects.movie.screenMovie.articleSearch)
    implementation(projects.data.analytics)
}