package com.rick.data.database_anime.model

import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "anime_fts_table")
@Fts4
data class AnimeFtsEntity(
    val animeId: Int,
    val title: String,
    val synopsis: String
)
