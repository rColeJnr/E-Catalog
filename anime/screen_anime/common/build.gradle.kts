plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.paging)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
}


android {

    namespace = "com.rick.anime.screen_anime.common"
}

dependencies {
    implementation(libs.bumptech.glide)
}