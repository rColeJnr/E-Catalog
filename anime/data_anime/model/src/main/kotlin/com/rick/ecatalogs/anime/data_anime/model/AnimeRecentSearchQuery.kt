package com.rick.ecatalogs.anime.data_anime.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class AnimeRecentSearchQuery(
    val query: String,
    val queriedDate: Instant = Clock.System.now()
)


