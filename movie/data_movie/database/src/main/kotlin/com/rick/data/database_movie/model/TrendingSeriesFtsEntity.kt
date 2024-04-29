package com.rick.data.database_movie.model

import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "trending_series_fts_table")
@Fts4
data class TrendingSeriesFtsEntity(
    val trendingSeriesId: Int,
    val title: String,
    val overview: String
)
