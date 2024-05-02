package com.rick.movie.data_movie.data.repository

import com.rick.data.analytics.AnalyticsEvent
import com.rick.data.analytics.AnalyticsHelper

internal fun AnalyticsHelper.logArticleFavoriteToggled(
    articleId: String,
    isFavorite: Boolean
) {
    val eventType = if (isFavorite) "article_saved" else "article_unsaved"
    val paramKey = if (isFavorite) "saved_article_id" else "unsaved_article_id"
    logEvent(
        AnalyticsEvent(
            type = eventType,
            extras = listOf(
                AnalyticsEvent.Param(key = paramKey, value = articleId),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logTrendingMovieFavoriteToggled(
    trendingMovieId: String,
    isFavorite: Boolean
) {
    val eventType = if (isFavorite) "trending_movie_saved" else "trending_movie_unsaved"
    val paramKey = if (isFavorite) "saved_trending_movie_id" else "unsaved_trending_movie_id"
    logEvent(
        AnalyticsEvent(
            type = eventType,
            extras = listOf(
                AnalyticsEvent.Param(key = paramKey, value = trendingMovieId),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logTrendingSeriesFavoriteToggled(
    trendingSeriesId: String,
    isFavorite: Boolean
) {
    val eventType = if (isFavorite) "trending_series_saved" else "trending_series_unsaved"
    val paramKey = if (isFavorite) "saved_trending_series_id" else "unsaved_trending_series_id"
    logEvent(
        AnalyticsEvent(
            type = eventType,
            extras = listOf(
                AnalyticsEvent.Param(key = paramKey, value = trendingSeriesId),
            ),
        ),
    )
}