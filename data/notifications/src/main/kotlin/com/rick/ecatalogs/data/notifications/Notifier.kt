package com.rick.ecatalogs.data.notifications

import com.rick.ecatalogs.game.data_game.model.Game
import com.rick.ecatalogs.book.data_book.model.bestseller.Bestseller
import com.rick.ecatalogs.book.data_book.model.bestseller.BestsellerGenre
import com.rick.ecatalogs.movie.data_movie.model.article_models.Article
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_movie.TrendingMovie
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_series.TrendingSeries

/**
 * Posts one notification per content category when background sync finds new content.
 * Calls with an empty list are ignored.
 */
interface Notifier {
    fun postTrendingMoviesNotifications(movies: List<TrendingMovie>)
    fun postTrendingSeriesNotifications(series: List<TrendingSeries>)
    fun postArticlesNotifications(articles: List<Article>)
    fun postBestsellersNotification(genre: BestsellerGenre, books: List<Bestseller>)

    /** "[title] is out today", opening the movie's details. */
    fun postReleaseReminder(movieId: Int, title: String)
    fun postComingSoonGamesNotification(games: List<Game>)
}
