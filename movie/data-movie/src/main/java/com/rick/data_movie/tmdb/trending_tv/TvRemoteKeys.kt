package com.rick.data_movie.tmdb.trending_tv

import androidx.room.Entity

@Entity(tableName = "trending_tv", primaryKeys = ["id"])
data class TvRemoteKeys(
    val id: Int,
    val prevKey: Int?,
    val nextKey: Int?
)