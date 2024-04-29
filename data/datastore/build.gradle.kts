plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.hilt)
    alias(libs.plugins.ecatalogs.android.room)
}

android {
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
    namespace = "com.rick.data.datastore"
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    api(libs.androidx.datastore.core)
    api(projects.data.datastoreProto)
    api(projects.data.common)
    api(projects.anime.dataAnime.model)
    api(projects.book.dataBook.model)
    api(projects.movie.dataMovie.model)
    api(projects.settings.dataSettings.model)

    testImplementation(libs.kotlinx.coroutines.test)
}
