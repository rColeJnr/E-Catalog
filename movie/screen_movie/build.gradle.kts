plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.paging)
}
//
dependencies {
    implementation(projects.movie.dataMovie)
}

android {
    externalNativeBuild {
        ndkBuild {
            path = file("src/main/jni/Android.mk") //Path of android.mk
        }
    }
    namespace = "com.rick.screen_movie"
}
