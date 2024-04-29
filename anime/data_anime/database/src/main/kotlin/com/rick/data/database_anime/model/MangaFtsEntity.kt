package com.rick.data.database_anime.model

import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "manga_fts_table")
@Fts4
data class MangaFtsEntity(
    val mangaId: Int,
    val title: String,
    val synopsis: String
)
