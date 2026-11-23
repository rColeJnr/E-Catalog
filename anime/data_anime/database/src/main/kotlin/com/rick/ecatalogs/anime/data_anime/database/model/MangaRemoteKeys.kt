package com.rick.ecatalogs.anime.data_anime.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga_remote_keys")
data class MangaRemoteKeys(
    @PrimaryKey val manga: Int,
    val prevKey: Int?,
    val nextKey: Int?
)
