package com.rick.ecatalogs.data.testing.notifications

import com.rick.ecatalogs.game.data_game.model.Game
import com.rick.ecatalogs.book.data_book.model.bestseller.Bestseller
import com.rick.ecatalogs.book.data_book.model.bestseller.BestsellerGenre
import com.rick.ecatalogs.data.notifications.Notifier
import com.rick.ecatalogs.movie.data_movie.model.article_models.Article
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_movie.TrendingMovie
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_series.TrendingSeries

/** Records what would have been posted. */
class TestNotifier : Notifier {

    val trendingMovies = mutableListOf<List<TrendingMovie>>()
    val trendingSeries = mutableListOf<List<TrendingSeries>>()
    val articles = mutableListOf<List<Article>>()
    val bestsellers = mutableListOf<Pair<BestsellerGenre, List<Bestseller>>>()

    override fun postTrendingMoviesNotifications(movies: List<TrendingMovie>) {
        trendingMovies.add(movies)
    }

    override fun postTrendingSeriesNotifications(series: List<TrendingSeries>) {
        trendingSeries.add(series)
    }

    override fun postArticlesNotifications(articles: List<Article>) {
        this.articles.add(articles)
    }

    override fun postBestsellersNotification(genre: BestsellerGenre, books: List<Bestseller>) {
        bestsellers.add(genre to books)
    }

    val comingSoonGames = mutableListOf<List<Game>>()
    override fun postComingSoonGamesNotification(games: List<Game>) {
        comingSoonGames.add(games)
    }

    val releaseReminders = mutableListOf<Pair<Int, String>>()

    override fun postReleaseReminder(movieId: Int, title: String) {
        releaseReminders.add(movieId to title)
    }
}
