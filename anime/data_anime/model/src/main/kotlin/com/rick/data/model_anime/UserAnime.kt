package com.rick.data.model_anime

import com.rick.data.model_anime.model_jikan.Aired
import com.rick.data.model_anime.model_jikan.Genre
import com.rick.data.model_anime.model_jikan.Trailer
import kotlinx.serialization.Serializable

@Serializable
data class UserAnime internal constructor(
    val id: Int,
    val url: String,
    val images: String,
    val trailer: Trailer,
    val title: String,
    val type: String,
    val source: String,
    val episodes: Int,
    val airing: Boolean,
    val aired: Aired?,
    val runtime: String,
    val rating: String,
    val score: Double,
    val scoredBy: Int,
    val rank: Int,
    val popularity: Int,
    val favorites: Int,
    val synopsis: String,
    val background: String,
    val season: String,
    val year: Int,
    val genres: List<Genre>,
    val isFavorite: Boolean,
) {
    constructor(anime: Anime, userData: AnimeUserData) : this(
        id = anime.id,
        url = anime.url,
        images = anime.images,
        trailer = anime.trailer,
        title = anime.title,
        type = anime.type,
        source = anime.source,
        episodes = anime.episodes,
        airing = anime.airing,
        aired = anime.aired,
        runtime = anime.runtime,
        rating = anime.rating,
        score = anime.score,
        scoredBy = anime.scoredBy,
        rank = anime.rank,
        popularity = anime.popularity,
        favorites = anime.favorites,
        synopsis = anime.synopsis,
        background = anime.background,
        season = anime.season,
        year = anime.year,
        genres = anime.genres,
        isFavorite = anime.id in userData.animeFavoriteIds
    )
}

fun List<Anime>.mapToUserAnime(userData: AnimeUserData): List<UserAnime> =
    map { UserAnime(it, userData) }

fun Anime.mapToUserAnime(userData: AnimeUserData): UserAnime =
    UserAnime(this, userData)