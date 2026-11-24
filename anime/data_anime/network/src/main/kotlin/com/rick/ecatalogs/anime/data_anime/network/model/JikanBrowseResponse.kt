package com.rick.ecatalogs.anime.data_anime.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Jikan v4 responses used by browsing and the richer details screens. Every field is nullable
// with a default: Jikan omits fields freely, and Gson leaves missing ones null.

/** `genres/anime`, `genres/manga`. */
@Serializable
data class JikanGenresResponse(val data: List<JikanGenreNetwork>? = null)

@Serializable
data class JikanGenreNetwork(
    @SerialName("mal_id") @SerializedName("mal_id") val id: Int,
    val name: String? = null,
)

/** `anime/{id}/full`: the anime with streaming services and broadcast time. */
@Serializable
data class AnimeFullResponse(val data: AnimeNetwork? = null)

/** `manga/{id}/full`. */
@Serializable
data class MangaFullResponse(val data: MangaNetwork? = null)

/** A streaming service ("Crunchyroll", url). */
@Serializable
data class JikanLinkNetwork(
    val name: String? = null,
    val url: String? = null,
)

/** When a currently airing anime airs, in Japan time (timezone "Asia/Tokyo"). */
@Serializable
data class JikanBroadcastNetwork(
    /** e.g. "Saturdays". */
    val day: String? = null,
    /** "HH:mm". */
    val time: String? = null,
    val timezone: String? = null,
    val string: String? = null,
)

/** `anime/{id}/episodes`. */
@Serializable
data class JikanEpisodesResponse(
    val data: List<JikanEpisodeNetwork>? = null,
    val pagination: JikanPagination? = null,
)

@Serializable
data class JikanEpisodeNetwork(
    /** The episode number. */
    @SerialName("mal_id") @SerializedName("mal_id") val number: Int,
    val title: String? = null,
    /** ISO date-time, e.g. "2024-01-06T00:00:00+00:00". */
    val aired: String? = null,
    val score: Double? = null,
    val filler: Boolean? = null,
    val recap: Boolean? = null,
)

@Serializable
data class JikanPagination(
    @SerialName("has_next_page") @SerializedName("has_next_page") val hasNextPage: Boolean? = null,
)

/** `anime/{id}/recommendations`, `manga/{id}/recommendations`. */
@Serializable
data class JikanRecommendationsResponse(val data: List<JikanRecommendationNetwork>? = null)

@Serializable
data class JikanRecommendationNetwork(
    val entry: JikanEntryNetwork? = null,
    val votes: Int? = null,
)

@Serializable
data class JikanEntryNetwork(
    @SerialName("mal_id") @SerializedName("mal_id") val id: Int,
    val title: String? = null,
    val images: JikanEntryImages? = null,
)

@Serializable
data class JikanEntryImages(val jpg: JikanEntryJpg? = null)

@Serializable
data class JikanEntryJpg(
    @SerialName("image_url") @SerializedName("image_url") val imageUrl: String? = null,
    @SerialName("large_image_url") @SerializedName("large_image_url") val largeImageUrl: String? = null,
)
