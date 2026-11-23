plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.hilt)
}

android {
    namespace = "com.rick.ecatalogs.data.notifications"
}

dependencies {

    api(projects.anime.dataAnime.model)
    api(projects.book.dataBook.model)
    api(projects.movie.dataMovie.model)

    implementation(libs.androidx.core)

}