package com.rick.data.model_book.gutenberg

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class GutenbergRecentSearchQuery(
    val query: String,
    val queriedDate: Instant = Clock.System.now()
)


