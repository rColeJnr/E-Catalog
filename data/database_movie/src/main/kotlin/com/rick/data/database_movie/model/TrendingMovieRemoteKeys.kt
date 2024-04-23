package com.rick.data.database_movie.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "trending_movie_remote_keys", primaryKeys = ["id"])
data class TrendingMovieRemoteKeys(
    val id: Int,
    @ColumnInfo("prev_key")
    val prevKey: Int?,
    @ColumnInfo("next_key")
    val nextKey: Int?
)