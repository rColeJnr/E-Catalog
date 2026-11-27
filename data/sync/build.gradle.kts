plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.hilt)
}

android {
    namespace = "com.rick.ecatalogs.data.sync"
}

dependencies {
    ksp(libs.hilt.ext.compiler)

    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)

    implementation(projects.data.analytics)
    implementation(projects.data.common)
    implementation(projects.book.dataBook.data)
    implementation(projects.data.datastore)
    implementation(projects.data.notifications)
    implementation(projects.movie.dataMovie.data)
    implementation(projects.game.dataGame.data)
    implementation(projects.anime.dataAnime.data)

    api(projects.data.util)
}
