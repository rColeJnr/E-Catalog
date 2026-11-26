plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.library.compose)
    alias(libs.plugins.ecatalogs.android.paging)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
}

android {
    namespace = "com.rick.ecatalogs.game.screen_game.common"
}

dependencies {
    api(projects.data.uiComponents.common)
    api(projects.game.dataGame.model)
    implementation(projects.data.uiDesign)
    implementation(libs.compose.coil)
}
