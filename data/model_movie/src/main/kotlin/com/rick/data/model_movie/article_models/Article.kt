package com.rick.data.model_movie.article_models

data class Article(
    val id: Long,
    val abstract: String,
    val webUrl: String,
    val leadParagraph: String,
    val source: String,
    val multimedia: List<Multimedia>,
    val headline: Headline,
    val pubDate: String,
    val byline: Byline,
)