plugins {
    alias(libs.plugins.ecatalogs.android.feature)
    alias(libs.plugins.ecatalogs.android.library.compose)
    alias(libs.plugins.ecatalogs.android.paging)
    alias(libs.plugins.ecatalogs.android.library.jacoco)
}

android {
    namespace = "com.rick.ecatalogs.game.screen_game.game_catalog"
}

dependencies {
    implementation(libs.lifecycle.runtimeCompose)
    implementation(projects.game.screenGame.common)
    implementation(projects.game.dataGame.data)
    implementation(projects.data.uiDesign)
    implementation(projects.data.analytics)
    implementation(projects.game.screenGame.gameDetails)
    implementation(projects.game.screenGame.gameSearch)
    implementation(projects.game.screenGame.gameFavorites)
}
