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

include("data:ui_components:common")
include("data:ui_components:auth")
include("data:ui_components:anime_favorite")
include("data:ui_components:book_favorite")
include("data:ui_components:movie_favorite")

include(":authentication:auth_data")
include(":authentication:auth_screen")

include(":settings:screen_settings")
include(":settings:data_settings:data")
include(":settings:data_settings:model")

include(":data:analytics")
include(":data:common")
include(":data:datastore")
include(":data:datastore_proto")

include(":anime:screen_anime:anime_catalog")
include(":anime:screen_anime:anime_details")
include(":anime:screen_anime:anime_favorites")
include(":anime:screen_anime:anime_search")
include(":anime:screen_anime:common")
include(":anime:screen_anime:manga_catalog")
include(":anime:screen_anime:manga_details")
include(":anime:screen_anime:manga_favorites")
include(":anime:screen_anime:manga_search")
include(":anime:data_anime:data")
include(":anime:data_anime:database")
include(":anime:data_anime:domain")
include(":anime:data_anime:model")
include(":anime:data_anime:network")

include(":book:screen_book:bestseller_catalog")
include(":book:screen_book:bestseller_favorites")
include(":book:screen_book:bestseller_search")
include(":book:screen_book:common")
include(":book:screen_book:gutenberg_catalog")
include(":book:screen_book:gutenberg_favorites")
include(":book:screen_book:gutenberg_search")
include(":book:data_book:data")
include(":book:data_book:database")
include(":book:data_book:domain")
include(":book:data_book:model")
include(":book:data_book:network")

include(":movie:screen_movie:common")
include(":movie:screen_movie:article_catalog")
include(":movie:screen_movie:article_favorite")
include(":movie:screen_movie:article_search")
include(":movie:screen_movie:trending_movie_catalog")
include(":movie:screen_movie:trending_movie_details")
include(":movie:screen_movie:trending_movie_favorite")
include(":movie:screen_movie:trending_movie_search")
include(":movie:screen_movie:trending_series_catalog")
include(":movie:screen_movie:trending_series_details")
include(":movie:screen_movie:trending_series_favorite")
include(":movie:screen_movie:trending_series_search")
include(":movie:data_movie:data")
include(":movie:data_movie:database")
include(":movie:data_movie:domain")
include(":movie:data_movie:model")
include(":movie:data_movie:network")
//include(":data:testing")