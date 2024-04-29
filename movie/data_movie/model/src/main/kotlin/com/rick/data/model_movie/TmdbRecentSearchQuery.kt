package com.rick.data.model_movie

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class TmdbRecentSearchQuery(
    val query: String,
    val queriedDate: Instant = Clock.System.now()
)


