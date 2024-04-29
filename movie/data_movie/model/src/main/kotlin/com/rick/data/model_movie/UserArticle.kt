package com.rick.data.model_movie

import com.rick.data.model_movie.article_models.Article
import com.rick.data.model_movie.article_models.Byline

data class UserArticle internal constructor(
    val id: Long,
    val abstract: String,
    val webUrl: String,
    val leadParagraph: String,
    val source: String,
    val multimedia: String,
    val headline: String,
    val pubDate: String,
    val byline: Byline,
    val isFavorite: Boolean
) {
    constructor(article: Article, userData: ArticleUserData) : this(
        id = article.id,
        abstract = article.abstract,
        webUrl = article.webUrl,
        leadParagraph = article.leadParagraph,
        source = article.source,
        multimedia = article.multimedia,
        headline = article.headline,
        pubDate = article.pubDate,
        byline = article.byline,
        isFavorite = article.id in userData.nyTimesArticlesFavoriteIds
    )
}

fun List<Article>.mapToUserArticle(userData: ArticleUserData): List<UserArticle> =
    map { UserArticle(it, userData) }

fun Article.mapToUserArticle(userData: ArticleUserData): UserArticle =
    UserArticle(this, userData)
