package com.rick.data.data_book

import com.rick.data.analytics.AnalyticsEvent
import com.rick.data.analytics.AnalyticsHelper

internal fun AnalyticsHelper.logBestsellerFavoriteToggled(
    bestsellerId: String,
    isFavorite: Boolean
) {
    val eventType = if (isFavorite) "bestseller_saved" else "bestseller_unsaved"
    val paramKey = if (isFavorite) "saved_bestseller_id" else "unsaved_bestseller_id"
    logEvent(
        AnalyticsEvent(
            type = eventType,
            extras = listOf(
                AnalyticsEvent.Param(key = paramKey, value = bestsellerId),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logGutenbergFavoriteToggled(
    gutenbergId: String,
    isFavorite: Boolean
) {
    val eventType = if (isFavorite) "gutenberg_saved" else "gutenberg_unsaved"
    val paramKey = if (isFavorite) "saved_gutenberg_id" else "unsaved_gutenberg_id"
    logEvent(
        AnalyticsEvent(
            type = eventType,
            extras = listOf(
                AnalyticsEvent.Param(key = paramKey, value = gutenbergId),
            ),
        ),
    )
}