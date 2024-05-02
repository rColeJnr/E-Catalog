package com.rick.data.model_movie.article_models

data class Article(
    val id: String,
    val abstract: String,
    val webUrl: String,
    val leadParagraph: String,
    val source: String,
    val multimedia: String,
    val headline: String,
    val pubDate: String,
    val byline: Byline,
)