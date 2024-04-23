pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "ecatalogs"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":data:common")
include(":ui_components:common")
include(":ui_components:auth")
include(":ui_components:anime_favorite")
include(":ui_components:book_favorite")
include(":ui_components:movie_favorite")
include(":movie:data_movie")
include(":movie:screen_movie")
include(":book:data_book")
include(":book:screen_book")
include(":anime:data_anime")
include(":anime:screen_anime")
include(":authentication:auth_data")
include(":authentication:auth_screen")
include(":data:datastore")
include(":data:datastore_proto")
include(":data:model_anime")
include(":data:model_book")
include(":data:model_movie")
include(":data:data_anime")
include(":data:data_book")
include(":data:data_movie")
include(":data:database_anime")
include(":data:database_book")
include(":data:database_movie")
include(":data:network_anime")
include(":data:network_book")
include(":data:network_movie")
//include(":data:testing")