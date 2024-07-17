plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.paging)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.hilt)
}


android {

    namespace = "com.rick.anime.screen_anime.common"
}

dependencies {
    implementation(projects.anime.dataAnime.domain)
    implementation(projects.data.analytics)
    implementation(projects.data.translation)

    implementation(libs.bumptech.glide)
}