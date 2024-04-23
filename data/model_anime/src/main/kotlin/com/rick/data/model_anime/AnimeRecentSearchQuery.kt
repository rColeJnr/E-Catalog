package com.rick.data.model_anime

import java.time.Clock
import java.time.Instant

data class AnimeRecentSearchQuery(
    val query: String,
    val queriedDate: Instant = Clock.systemDefaultZone().instant()
)


