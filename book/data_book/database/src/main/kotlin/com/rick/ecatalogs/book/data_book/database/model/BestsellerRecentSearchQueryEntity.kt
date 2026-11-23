package com.rick.ecatalogs.book.data_book.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "bestseller_recent_search_queries_table")
data class BestsellerRecentSearchQueryEntity(
    @PrimaryKey
    val query: String,
    val queriedDate: Instant
)
