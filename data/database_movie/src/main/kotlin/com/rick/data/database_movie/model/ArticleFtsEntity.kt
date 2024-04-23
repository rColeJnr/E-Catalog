package com.rick.data.database_movie.model

import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "article_fts_table")
@Fts4
data class ArticleFtsEntity(
    val articleId: Int,
    val title: String,
    val summary: String
)
