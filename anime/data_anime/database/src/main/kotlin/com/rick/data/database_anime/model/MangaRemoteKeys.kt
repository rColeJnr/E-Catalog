package com.rick.data.database_anime.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga_remote_keys")
data class MangaRemoteKeys(
    @PrimaryKey val manga: Int,
    val prevKey: Int?,
    val nextKey: Int?
)
