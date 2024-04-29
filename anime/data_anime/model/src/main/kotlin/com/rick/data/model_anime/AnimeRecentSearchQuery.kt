package com.rick.data.model_anime

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class AnimeRecentSearchQuery(
    val query: String,
    val queriedDate: Instant = Clock.System.now()
)


