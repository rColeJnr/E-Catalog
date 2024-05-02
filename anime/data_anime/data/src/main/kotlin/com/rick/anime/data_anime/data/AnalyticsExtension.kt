package com.rick.anime.data_anime.data

import com.rick.data.analytics.AnalyticsEvent
import com.rick.data.analytics.AnalyticsHelper


internal fun AnalyticsHelper.logAnimeFavoriteToggled(
    animeId: String,
    isFavorite: Boolean
) {
    val eventType = if (isFavorite) "anime_saved" else "anime_unsaved"
    val paramKey = if (isFavorite) "saved_anime_id" else "unsaved_anime_id"
    logEvent(
        AnalyticsEvent(
            type = eventType,
            extras = listOf(
                AnalyticsEvent.Param(key = paramKey, value = animeId),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logMangaFavoriteToggled(
    mangaId: String,
    isFavorite: Boolean
) {
    val eventType = if (isFavorite) "manga_saved" else "manga_unsaved"
    val paramKey = if (isFavorite) "saved_manga_id" else "unsaved_manga_id"
    logEvent(
        AnalyticsEvent(
            type = eventType,
            extras = listOf(
                AnalyticsEvent.Param(key = paramKey, value = mangaId),
            ),
        ),
    )
}