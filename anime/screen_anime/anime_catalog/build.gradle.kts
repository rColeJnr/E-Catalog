plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.paging)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
//    alias(libs.plugins.ecatalogs.android.room)
}
//
dependencies {
    implementation(projects.anime.screenAnime.common)
    implementation(projects.data.uiComponents.common)
    implementation(projects.anime.screenAnime.animeDetails)
    implementation(projects.anime.screenAnime.animeFavorites)
    implementation(projects.anime.screenAnime.animeSearch)
    implementation(projects.anime.dataAnime.model)
    implementation(projects.anime.dataAnime.data)
    implementation(projects.data.analytics)
}

android {
    defaultConfig {
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
    externalNativeBuild {
        ndkBuild {
            path = file("src/main/jni/Android.mk") //Path of android.mk
        }
    }
    namespace = "com.rick.anime.screen_anime.anime_catalog"
}
