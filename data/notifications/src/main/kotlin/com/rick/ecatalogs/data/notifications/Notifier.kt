package com.rick.ecatalogs.data.notifications

import com.rick.ecatalogs.movie.data_movie.model.article_models.Article
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_movie.TrendingMovie
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_series.TrendingSeries

interface Notifier {
    fun postTrendingMoviesNotifications(movies: List<TrendingMovie>)
    fun postTrendingSeriesNotifications(series: List<TrendingSeries>)
    fun postArticlesNotifications(articles: List<Article>)
}