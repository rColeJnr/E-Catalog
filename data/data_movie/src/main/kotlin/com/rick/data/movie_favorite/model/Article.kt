package com.rick.data.movie_favorite.model

import android.annotation.SuppressLint
import com.rick.data.database_movie.model.ArticleEntity
import com.rick.data.model_movie.article_models.Byline
import com.rick.data.network_movie.model.ArticleNetwork
import java.text.SimpleDateFormat
import java.util.Date

fun ArticleNetwork.asArticleEntity() = ArticleEntity(
    abstract = abstract,
    webUrl = webUrl,
    leadParagraph = leadParagraph,
    source = source,
    multimedia = multimedia,
    headline = headline,
    pubDate = pubDate,
    byline = byline ?: Byline("null")
)

@SuppressLint("SimpleDateFormat")
fun String.toDateFormat(): Date =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(this)!!

@SuppressLint("SimpleDateFormat")
fun Date.toString(): String =
    SimpleDateFormat("yyyy-MM-dd").format(this)