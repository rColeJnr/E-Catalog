package com.rick.ecatalogs.movie.data_movie.data.model

import com.rick.ecatalogs.data.common.di.toDateFormat
import com.rick.ecatalogs.movie.data_movie.database.model.ArticleEntity
import com.rick.ecatalogs.movie.data_movie.database.model.ArticleRecentSearchQueryEntity
import com.rick.ecatalogs.movie.data_movie.model.ArticleRecentSearchQuery
import com.rick.ecatalogs.movie.data_movie.model.article_models.Byline
import com.rick.ecatalogs.movie.data_movie.network.model.ArticleNetwork

fun ArticleNetwork.asArticleEntity() = ArticleEntity(
    id = id,
    abstract = abstract,
    webUrl = webUrl,
    leadParagraph = leadParagraph,
    source = source,
    multimedia = multimedia?.default?.url ?: "",
    title = headline.main ?: "Missing headline",
    pubDate = pubDate.toDateFormat(),
    byline = byline ?: Byline("Unknown author")
)

fun ArticleRecentSearchQueryEntity.asExternalModel() = ArticleRecentSearchQuery(
    query = query,
    queriedDate = queriedDate,
)