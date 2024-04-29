package com.rick.data.database_anime.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(tableName = "anime_recent_search_queries_table")
data class AnimeRecentSearchQueryEntity(
    @PrimaryKey
    val query: String,
    val queriedDate: Instant
)
