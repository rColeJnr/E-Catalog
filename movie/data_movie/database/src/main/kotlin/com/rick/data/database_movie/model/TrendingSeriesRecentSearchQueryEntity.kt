package com.rick.data.database_movie.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(tableName = "trending_series_recent_search_queries_table")
data class TrendingSeriesRecentSearchQueryEntity(
    @PrimaryKey
    val query: String,
    val queriedDate: Instant
)
