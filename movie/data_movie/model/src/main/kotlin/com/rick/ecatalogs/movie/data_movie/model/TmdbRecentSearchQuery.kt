package com.rick.ecatalogs.movie.data_movie.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class TmdbRecentSearchQuery(
    val query: String,
    val queriedDate: Instant = Clock.System.now()
)


