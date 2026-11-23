package com.rick.ecatalogs.anime.data_anime.network.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeDto(
    @SerialName("data")
    val `data`: List<MyAnimeNetwork>,
    @SerialName("pagination")
    val pagination: com.rick.ecatalogs.anime.data_anime.model.model_jikan.dto.Pagination
) {
    @Serializable
    data class MyAnimeNetwork(
        @SerialName("aired")
        val aired: com.rick.ecatalogs.anime.data_anime.model.model_jikan.dto.Aired?,
        @SerialName("airing")
        val airing: Boolean?,
        @SerialName("approved")
        val approved: Boolean?,
        @SerialName("background")
        val background: String?,
        @SerialName("broadcast")
        val broadcast: com.rick.ecatalogs.anime.data_anime.model.model_jikan.dto.Broadcast?,
        @SerialName("demographics")
        val demographics: List<com.rick.ecatalogs.anime.data_anime.model.model_jikan.dto.Demographic?>,
        @SerialName("duration")
        val duration: String?,
        @SerialName("episodes")
        val episodes: Int?,
        @SerialName("explicit_genres")
        val explicitGenres: List<com.rick.ecatalogs.anime.data_anime.model.model_jikan.dto.Genre?>,
        @SerialName("favorites")
        val favorites: Int?,
        @SerialName("genres")
        val genres: List<com.rick.ecatalogs.anime.data_anime.model.model_jikan.dto.Genre?>,
        @SerialName("images")
        val images: com.rick.ecatalogs.anime.data_anime.model.model_jikan.dto.Images?,
        @SerialName("licensors")
        val licensors: List<com.rick.ecatalogs.anime.data_anime.model.model_jikan.dto.Licensor>?,
        @SerialName("mal_id")
        val malId: Int,
        @SerialName("members")
        val members: Int,
        @SerialName("popularity")
        val popularity: Int,
        @SerialName("producers")
        val producers: List<com.rick.ecatalogs.anime.data_anime.model.model_jikan.dto.Producer>?,
        @SerialName("rank")
        val rank: Int?,
        @SerialName("rating")
        val rating: String?,
        @SerialName("score")
        val score: Double?,
        @SerialName("scored_by")
        val scoredBy: Int?,
        @SerialName("season")
        val season: String?,
        @SerialName("source")
        val source: String?,
        @SerialName("status")
        val status: String?,
        @SerialName("studios")
        val studios: List<com.rick.ecatalogs.anime.data_anime.model.model_jikan.dto.Studio>?,
        @SerialName("synopsis")
        val synopsis: String?,
        @SerialName("themes")
        val themes: List<com.rick.ecatalogs.anime.data_anime.model.model_jikan.dto.Theme>?,
        @SerialName("title")
        val title: String?,
        @SerialName("title_english")
        val titleEnglish: String?,
        @SerialName("title_japanese")
        val titleJapanese: String?,
        @SerialName("title_synonyms")
        val titleSynonyms: List<String>?,
        @SerialName("titles")
        val titles: List<com.rick.ecatalogs.anime.data_anime.model.model_jikan.dto.Title>?,
        @SerialName("trailer")
        val trailer: com.rick.ecatalogs.anime.data_anime.model.model_jikan.dto.Trailer?,
        @SerialName("type")
        val type: String?,
        @SerialName("url")
        val url: String?,
        @SerialName("year")
        val year: Int?
    )
}