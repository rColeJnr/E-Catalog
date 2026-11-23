package com.rick.ecatalogs.movie.data_movie.database.model

import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "article_fts_table")
@Fts4
data class ArticleFtsEntity(
    val articleId: String,
    val title: String,
    val summary: String
)
