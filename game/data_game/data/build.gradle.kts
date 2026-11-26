plugins {
    alias(libs.plugins.ecatalogs.android.library)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
    alias(libs.plugins.ecatalogs.android.hilt)
    alias(libs.plugins.ecatalogs.android.room)
}

android {
    namespace = "com.rick.ecatalogs.game.data_game.data"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    api(projects.game.dataGame.model)
    implementation(projects.data.datastore)
    implementation(projects.data.analytics)
    implementation(projects.game.dataGame.database)
    implementation(projects.game.dataGame.network)

    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.paging.common)
    implementation(libs.square.converter.gson)

    testImplementation(libs.kotlinx.coroutines.test)
}
