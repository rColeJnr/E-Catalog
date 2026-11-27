package com.rick.ecatalogs.anime.data_anime.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MangaNetwork(
    @SerializedName("malId")
    @SerialName("malId")
    val id: Int,
    val url: String? = null,
    val title: String? = null,
    val titleEnglish: String? = null,
    val imageUrl: String? = null,
    val images: EdgeImages? = null,
    val synopsis: String? = null,
    val background: String? = null,
    val type: String? = null,
    val chapters: Int? = null,
    val volumes: Int? = null,
    val status: String? = null,
    val publishing: Boolean? = null,
    val published: EdgeDateRange? = null,
    val score: Double? = null,
    val scoredBy: Int? = null,
    val rank: Int? = null,
    val popularity: Int? = null,
    val members: Int? = null,
    val favorites: Int? = null,
    val authors: List<EdgeRef>? = null,
    val serializations: List<EdgeRef>? = null,
    val genres: List<EdgeRef>? = null,
    val themes: List<EdgeRef>? = null,
    val demographics: List<EdgeRef>? = null,
    val relations: List<JikanRelationNetwork>? = null,
    val startDate: String? = null,
    val endDate: String? = null,
)

val MangaNetwork.posterUrl: String?
    get() = images?.medium ?: imageUrl ?: images?.large ?: images?.small
