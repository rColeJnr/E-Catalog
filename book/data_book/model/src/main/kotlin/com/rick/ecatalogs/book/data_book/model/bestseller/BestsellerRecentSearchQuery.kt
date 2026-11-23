package com.rick.ecatalogs.book.data_book.model.bestseller

import java.time.Clock
import java.time.Instant

data class BestsellerRecentSearchQuery(
    val query: String,
    val queriedDate: Instant = Clock.systemDefaultZone().instant()
)


