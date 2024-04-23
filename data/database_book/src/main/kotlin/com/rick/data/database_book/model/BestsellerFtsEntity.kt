package com.rick.data.database_book.model

import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "bestseller_fts_table")
@Fts4
data class BestsellerFtsEntity(
    val bestsellerId: Int,
    val title: String,
    val summary: String
)
