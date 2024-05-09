package com.rick.data.database_movie.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rick.core.di.toDateString
import com.rick.data.model_movie.article_models.Article
import com.rick.data.model_movie.article_models.Byline
import java.util.Date

@Entity(tableName = "article_table")
data class ArticleEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val abstract: String,
    @ColumnInfo("web_url")
    val webUrl: String,
    @ColumnInfo("lead_paragraph")
    val leadParagraph: String,
    val source: String,
    val multimedia: String,
    val title: String,
    @ColumnInfo("publication_date")
    val pubDate: Date,
    val byline: Byline,
)

fun ArticleEntity.asArticle() = Article(
    id = id,
    abstract = abstract,
    webUrl = webUrl,
    leadParagraph = leadParagraph,
    source = source,
    multimedia = multimedia,
    headline = title,
    pubDate = pubDate.toDateString(),
    byline = byline
)
