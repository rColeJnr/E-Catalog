package com.rick.data.database_movie.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "trending_series_remote_keys", primaryKeys = ["id"])
data class TrendingSeriesRemoteKeys(
    val id: Int,
    @ColumnInfo("prev_key")
    val prevKey: Int?,
    @ColumnInfo("next_key")
    val nextKey: Int?
)