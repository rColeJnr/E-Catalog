package com.rick.data_movie.tmdb.trending_tv

import androidx.room.Entity

@Entity(tableName = "tv_remote_keys", primaryKeys = ["id"])
data class TvRemoteKeys(
    val id: Int,
    val prevKey: Int?,
    val nextKey: Int?
)