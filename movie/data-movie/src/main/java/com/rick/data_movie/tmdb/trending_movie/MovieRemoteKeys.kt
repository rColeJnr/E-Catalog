package com.rick.data_movie.tmdb.trending_movie

import androidx.room.Entity

@Entity(tableName = "trending_movie", primaryKeys = ["id"])
data class MovieRemoteKeys(
    val id: Int,
    val prevKey: Int?,
    val nextKey: Int?
)