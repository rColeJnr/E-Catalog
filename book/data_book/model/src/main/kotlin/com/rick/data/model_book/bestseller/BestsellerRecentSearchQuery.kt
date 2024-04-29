package com.rick.data.model_book.bestseller

import java.time.Clock
import java.time.Instant

data class BestsellerRecentSearchQuery(
    val query: String,
    val queriedDate: Instant = Clock.systemDefaultZone().instant()
)


