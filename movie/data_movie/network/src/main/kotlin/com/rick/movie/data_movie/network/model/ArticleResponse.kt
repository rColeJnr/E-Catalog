package com.rick.movie.data_movie.network.model

import kotlinx.serialization.Serializable


@Serializable
data class ArticleResult(
    val docs: List<ArticleNetwork>,
    val meta: Meta
)

@Serializable
data class ArticleResponse(
    val response: ArticleResult
)

@Serializable
data class Meta(
    val offset: Int
)