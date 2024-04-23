package com.rick.data.network_anime.model

import com.google.gson.annotations.SerializedName
import com.rick.data.model_anime.model_jikan.Author
import com.rick.data.model_anime.model_jikan.Genre
import com.rick.data.model_anime.model_jikan.Images
import com.rick.data.model_anime.model_jikan.Published
import com.rick.data.model_anime.model_jikan.Serialization
import com.rick.data.model_anime.model_jikan.Theme
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MangaNetwork(
    @SerializedName("mal_id")
    @SerialName("mal_id")
    val id: Int,
    val url: String,
    val images: Images,
    val title: String,
    @SerializedName("title_english")
    @SerialName("title_english")
    val titleEnglish: String?,
    val type: String,
    val chapters: Int?,
    val volumes: Int?,
    val publishing: Boolean,
    val published: Published,
    val score: Double,
    @SerializedName("scored_by")
    @SerialName("scored_by")
    val scoredBy: Int,
    val rank: Int,
    val popularity: Int,
    val favorites: Int,
    val synopsis: String,
    val background: String?,
    val authors: List<Author>,
    val serializations: List<Serialization>,
    val genres: List<Genre>,
    val themes: List<Theme>,
)
