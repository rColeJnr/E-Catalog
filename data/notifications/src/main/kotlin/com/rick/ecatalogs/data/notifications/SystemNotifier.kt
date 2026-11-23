package com.rick.ecatalogs.data.notifications

import android.content.Context
import com.rick.ecatalogs.movie.data_movie.model.article_models.Article
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_movie.TrendingMovie
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_series.TrendingSeries
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SystemNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
): Notifier {

    override fun postTrendingMoviesNotifications(movies: List<TrendingMovie>) {
        TODO("Not yet implemented")
    }

    override fun postTrendingSeriesNotifications(series: List<TrendingSeries>) {
        TODO("Not yet implemented")
    }

    override fun postArticlesNotifications(articles: List<Article>) {
        TODO("Not yet implemented")
    }


}