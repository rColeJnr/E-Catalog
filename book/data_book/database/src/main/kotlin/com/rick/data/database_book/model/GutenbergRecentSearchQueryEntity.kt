package com.rick.data.database_book.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(tableName = "gutenberg_recent_search_queries_table")
data class GutenbergRecentSearchQueryEntity(
    @PrimaryKey
    val query: String,
    val queriedDate: Instant
)
