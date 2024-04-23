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
    api(projects.data.modelAnime)
    api(projects.data.modelBook)
    api(projects.data.modelMovie)

    testImplementation(libs.kotlinx.coroutines.test)
}
