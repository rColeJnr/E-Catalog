package com.rick.ecatalogs.data.notifications

import com.rick.ecatalogs.book.data_book.model.bestseller.BestsellerGenre

/** Deep links declared on the catalog fragments' nav graphs. */
object DeepLinks {
    const val TRENDING_MOVIE_CATALOG = "com.rick.ecs://trending_movie_catalog"
    const val TRENDING_SERIES_CATALOG = "com.rick.ecs://trending_series_catalog"
    const val ARTICLE_CATALOG = "com.rick.ecs://article_catalog"
    const val GAME_CATALOG = "com.rick.ecs://game_catalog"
    const val ANIME_CATALOG = "com.rick.ecs://anime_catalog"
    const val MANGA_CATALOG = "com.rick.ecs://manga_catalog"
    const val BESTSELLER_CATALOG = "com.rick.ecs://bestseller-catalog-screen"
    const val BESTSELLER_GENRE_ARG = "genre"

    fun movieDetails(movieId: Int) = "com.rick.ecs://trending_movie_details_fragment/$movieId"

    fun bestsellerCatalog(genre: BestsellerGenre) =
        "$BESTSELLER_CATALOG?$BESTSELLER_GENRE_ARG=${genre.listName}"
}
