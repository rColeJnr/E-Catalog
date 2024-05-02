package com.rick.movie.data_movie.data.model

import android.annotation.SuppressLint
import com.rick.data.database_movie.model.ArticleEntity
import com.rick.data.database_movie.model.ArticleRecentSearchQueryEntity
import com.rick.data.model_movie.ArticleRecentSearchQuery
import com.rick.data.model_movie.article_models.Byline
import com.rick.movie.data_movie.network.model.ArticleNetwork
import java.text.SimpleDateFormat
import java.util.Date

fun ArticleNetwork.asArticleEntity() = ArticleEntity(
    id = id,
    abstract = abstract,
    webUrl = webUrl,
    leadParagraph = leadParagraph,
    source = source,
    multimedia = multimedia?.getOrNull(3)?.url ?: "",
    title = headline.main ?: "Missing headline",
    pubDate = pubDate,
    byline = byline ?: Byline("Unknown author")
)

@SuppressLint("SimpleDateFormat")
fun String.toDateFormat(): Date =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(this)!!

@SuppressLint("SimpleDateFormat")
fun Date.toString(): String =
    SimpleDateFormat("yyyy-MM-dd").format(this)

fun ArticleRecentSearchQueryEntity.asExternalModel() = ArticleRecentSearchQuery(
    query = query,
    queriedDate = queriedDate,
)