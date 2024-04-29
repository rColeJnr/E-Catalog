package com.rick.data.model_anime

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class MangaRecentSearchQuery(
    val query: String,
    val queriedDate: Instant = Clock.System.now()
)


