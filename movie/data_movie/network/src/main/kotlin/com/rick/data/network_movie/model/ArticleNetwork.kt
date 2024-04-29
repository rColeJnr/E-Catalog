package com.rick.data.network_movie.model

import com.google.gson.annotations.SerializedName
import com.rick.data.model_movie.article_models.Byline
import com.rick.data.model_movie.article_models.Headline
import com.rick.data.model_movie.article_models.Multimedia
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleNetwork(
    val abstract: String,
    @SerializedName("web_url")
    @SerialName("web_url")
    val webUrl: String,
    @SerializedName("lead_paragraph")
    @SerialName("lead_paragraph")
    val leadParagraph: String,
    val source: String,
    val multimedia: List<Multimedia>?,
    val headline: Headline,
    @SerializedName("pub_date")
    @SerialName("pub_date")
    val pubDate: String,
    val byline: Byline? = null,
)
