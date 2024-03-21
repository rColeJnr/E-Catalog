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
include(":core")
include(":ui_components:common")
include(":ui_components:auth")
include(":ui_components:favorite")
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
include(":data:model_book")
include(":data:model_movie")
include(":data:model_anime")
include(":data:model_favorite")
//include(":data:testing")