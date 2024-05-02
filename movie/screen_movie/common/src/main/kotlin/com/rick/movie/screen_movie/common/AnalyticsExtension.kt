package com.rick.movie.screen_movie.common

import com.rick.data.analytics.AnalyticsEvent
import com.rick.data.analytics.AnalyticsHelper

fun AnalyticsHelper.logScreenView(screenName: String) {
    logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.SCREEN_VIEW,
            extras = listOf(
                AnalyticsEvent.Param(AnalyticsEvent.ParamKeys.SCREEN_NAME, screenName),
            ),
        ),
    )
}

fun AnalyticsHelper.logArticleOpened(articleId: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "article_opened",
            extras = listOf(
                AnalyticsEvent.Param("opened_article", articleId),
            ),
        ),
    )
}

fun AnalyticsHelper.logTrendingMovieOpened(trendingMovieId: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "trendingMovie_opened",
            extras = listOf(
                AnalyticsEvent.Param("opened_trendingMovie", trendingMovieId),
            ),
        ),
    )
}

fun AnalyticsHelper.logTrendingSeriesOpened(trendingSeriesId: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "trendingSeries_opened",
            extras = listOf(
                AnalyticsEvent.Param("opened_trendingSeries", trendingSeriesId),
            ),
        ),
    )
}
