package com.rick.movie.data_movie.data.model

import com.rick.core.di.toDateFormat
import com.rick.data.database_movie.model.ArticleEntity
import com.rick.data.database_movie.model.ArticleRecentSearchQueryEntity
import com.rick.data.model_movie.ArticleRecentSearchQuery
import com.rick.data.model_movie.article_models.Byline
import com.rick.movie.data_movie.network.model.ArticleNetwork

fun ArticleNetwork.asArticleEntity() = ArticleEntity(
    id = id,
    abstract = abstract,
    webUrl = webUrl,
    leadParagraph = leadParagraph,
    source = source,
    multimedia = multimedia?.getOrNull(3)?.url ?: "",
    title = headline.main ?: "Missing headline",
    pubDate = pubDate.toDateFormat(),
    byline = byline ?: Byline("Unknown author")
)

fun ArticleRecentSearchQueryEntity.asExternalModel() = ArticleRecentSearchQuery(
    query = query,
    queriedDate = queriedDate,
)