plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.library.compose)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
}

android {
    namespace = "com.rick.ecatalogs.game.screen_game.game_details"
}

dependencies {
    implementation(libs.lifecycle.runtimeCompose)
    implementation(libs.compose.coil)
    implementation(projects.game.screenGame.common)
    implementation(projects.game.dataGame.data)
    implementation(projects.data.uiDesign)
    implementation(projects.data.translation)
    implementation(projects.data.trailer)
}
