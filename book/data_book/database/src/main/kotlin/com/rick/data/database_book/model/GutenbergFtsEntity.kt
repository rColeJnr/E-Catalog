package com.rick.data.database_book.model

import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "gutenberg_fts_table")
@Fts4
data class GutenbergFtsEntity(
    val gutenbergId: Int,
    val title: String,
    val summary: String
)
