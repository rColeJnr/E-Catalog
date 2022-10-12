package com.rick.data_anime

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga_remote_keys")
data class MangaRemoteKeys(
    @PrimaryKey val manga: Int,
    val prevKey: Int?,
    val nextKey: Int?
)
