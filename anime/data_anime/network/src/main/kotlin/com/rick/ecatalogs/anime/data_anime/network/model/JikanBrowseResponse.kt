package com.rick.ecatalogs.anime.data_anime.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `genres/anime`, `genres/manga`. */
@Serializable
data class JikanGenresResponse(
    val data: List<JikanGenreNetwork>? = null,
    val meta: EdgeMeta? = null,
)

@Serializable
data class JikanGenreNetwork(
    @SerialName("malId") @SerializedName("malId") val id: Int,
    val name: String? = null,
)

/** `anime/{id}`. */
@Serializable
data class AnimeFullResponse(
    val data: AnimeNetwork? = null,
    val meta: EdgeMeta? = null,
)

/** `manga/{id}`. */
@Serializable
data class MangaFullResponse(
    val data: MangaNetwork? = null,
    val meta: EdgeMeta? = null,
)

/** A streaming service ("Crunchyroll", url). */
@Serializable
data class JikanLinkNetwork(
    val name: String? = null,
    val url: String? = null,
)

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
    val meta: EdgeMeta? = null,
)

@Serializable
data class JikanEpisodeNetwork(
    val number: Int,
    val title: String? = null,
    val aired: String? = null,
    val score: Double? = null,
    val filler: Boolean? = null,
    val recap: Boolean? = null,
)

/** `anime/{id}/recommendations`, `manga/{id}/recommendations`. */
@Serializable
data class JikanRecommendationsResponse(
    val data: List<JikanRecommendationNetwork>? = null,
    val meta: EdgeMeta? = null,
)

@Serializable
data class JikanRecommendationNetwork(
    @SerialName("malId") @SerializedName("malId") val id: Int,
    val title: String? = null,
    val imageUrl: String? = null,
    val votes: Int? = null,
)

@Serializable
data class JikanRelationNetwork(
    /** e.g. "Adaptation", "Sequel", "Prequel", "Side story". */
    val relation: String? = null,
    @SerialName("malId") @SerializedName("malId") val id: Int? = null,
    /** "anime" or "manga". */
    val type: String? = null,
    val title: String? = null,
)

/** `anime/{id}/characters`. */
@Serializable
data class JikanCharactersResponse(
    val data: List<JikanCharacterNetwork>? = null,
    val meta: EdgeMeta? = null,
)

@Serializable
data class JikanCharacterNetwork(
    @SerialName("malId") @SerializedName("malId") val id: Int? = null,
    val name: String? = null,
    val imageUrl: String? = null,
    /** "Main" or "Supporting". */
    val role: String? = null,
    val voiceActors: List<JikanVoiceActorNetwork>? = null,
)

@Serializable
data class JikanVoiceActorNetwork(
    @SerialName("malId") @SerializedName("malId") val id: Int? = null,
    val name: String? = null,
    val language: String? = null,
    val imageUrl: String? = null,
)

/** `people/{id}/full`: a voice actor with their roles. */
@Serializable
data class JikanPersonResponse(
    val data: JikanPersonNetwork? = null,
    val meta: EdgeMeta? = null,
)

@Serializable
data class JikanPersonNetwork(
    @SerialName("malId") @SerializedName("malId") val id: Int,
    val name: String? = null,
    val givenName: String? = null,
    val familyName: String? = null,
    val birthDate: String? = null,
    val about: String? = null,
    val imageUrl: String? = null,
    val images: EdgeImages? = null,
    val voices: List<JikanVoiceRoleNetwork>? = null,
)

@Serializable
data class JikanVoiceRoleNetwork(
    val animeId: Int? = null,
    val animeTitle: String? = null,
    val animeImageUrl: String? = null,
    val characterId: Int? = null,
    val characterName: String? = null,
    val characterRole: String? = null,
)
