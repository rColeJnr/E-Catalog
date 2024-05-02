package com.rick.data.network_anime.model

import com.google.gson.annotations.SerializedName
import com.rick.data.model_anime.model_jikan.Aired
import com.rick.data.model_anime.model_jikan.Genre
import com.rick.data.model_anime.model_jikan.Images
import com.rick.data.model_anime.model_jikan.Trailer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AnimeNetwork(
    @SerializedName("mal_id")
    @SerialName("mal_id")
    val id: Int,
    val url: String?,
    val images: Images?,
    val trailer: Trailer?,
    val title: String?,
    @SerializedName("title_english")
    @SerialName("title_english")
    val titleEnglish: String?,
    val type: String?,
    val source: String?,
    val episodes: Int?,
    val airing: Boolean?,
    val aired: Aired?,
    val duration: String?,
    val rating: String?,
    val score: Double?,
    @SerializedName("scored_by")
    @SerialName("scored_by")
    val scoredBy: Int?,
    val rank: Int?,
    val popularity: Int?,
    val favorites: Int?,
    val synopsis: String?,
    val background: String?,
    val season: String?,
    val year: Int?,
    val genres: List<Genre> = listOf(),
)

