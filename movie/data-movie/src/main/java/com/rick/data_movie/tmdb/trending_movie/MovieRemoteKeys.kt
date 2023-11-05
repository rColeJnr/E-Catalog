package com.rick.data_movie.tmdb.trending_movie

import androidx.room.Entity

@Entity(tableName = "movie_remote_keys", primaryKeys = ["id"])
data class MovieRemoteKeys(
    val id: Int,
    val prevKey: Int?,
    val nextKey: Int?
)