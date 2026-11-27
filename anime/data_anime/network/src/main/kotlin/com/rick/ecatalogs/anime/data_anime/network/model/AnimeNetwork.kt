package com.rick.ecatalogs.anime.data_anime.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeNetwork(
    @SerializedName("malId")
    @SerialName("malId")
    val id: Int,
    val url: String? = null,
    val title: String? = null,
    val titleEnglish: String? = null,
    val imageUrl: String? = null,
    val images: EdgeImages? = null,
    val trailer: EdgeTrailerNetwork? = null,
    val synopsis: String? = null,
    val background: String? = null,
    val type: String? = null,
    val source: String? = null,
    val episodes: Int? = null,
    val status: String? = null,
    val airing: Boolean? = null,
    val aired: EdgeDateRange? = null,
    val duration: String? = null,
    val rating: String? = null,
    val score: Double? = null,
    val scoredBy: Int? = null,
    val rank: Int? = null,
    val popularity: Int? = null,
    val members: Int? = null,
    val favorites: Int? = null,
    val season: String? = null,
    val year: Int? = null,
    val genres: List<EdgeRef>? = null,
    val themes: List<EdgeRef>? = null,
    val demographics: List<EdgeRef>? = null,
    val studios: List<EdgeRef>? = null,
    val broadcast: JikanBroadcastNetwork? = null,
    val relations: List<JikanRelationNetwork>? = null,
    val streaming: List<JikanLinkNetwork>? = null,
    val startDate: String? = null,
    val endDate: String? = null,
)

val AnimeNetwork.posterUrl: String?
    get() = images?.medium ?: imageUrl ?: images?.large ?: images?.small

@Serializable
data class EdgeTrailerNetwork(
    val youtubeId: String? = null,
    val url: String? = null,
    val embedUrl: String? = null,
)
