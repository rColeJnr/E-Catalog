package com.rick.data_anime

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime_remote_keys")
data class AnimeRemoteKeys(
    @PrimaryKey val anime: Int,
    val prevKey: Int?,
    val nextKey: Int?
)
