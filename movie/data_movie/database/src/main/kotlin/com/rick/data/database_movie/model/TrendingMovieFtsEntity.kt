package com.rick.data.database_movie.model

import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "trending_movie_fts_table")
@Fts4
data class TrendingMovieFtsEntity(
    val trendingMovieId: Int,
    val title: String,
    val overview: String
)
