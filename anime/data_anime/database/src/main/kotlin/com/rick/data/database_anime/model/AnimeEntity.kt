package com.rick.data.database_anime.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rick.data.model_anime.Anime

@Entity(tableName = "anime_table")
data class AnimeEntity(
    @PrimaryKey
    val id: Int,
    val url: String,
    val images: String,
    val trailer: String,
    val title: String,
    val type: String,
    val source: String,
    val episodes: Int,
    val airing: Boolean,
    val aired: String,
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
    val genres: List<String>,
)

fun AnimeEntity.asAnime() = Anime(
    id = id,
    url = url,
    images = images,
    trailer = trailer,
    title = title,
    type = type,
    source = source,
    episodes = episodes,
    airing = airing,
    aired = aired,
    runtime = runtime,
    rating = rating,
    score = score,
    scoredBy = scoredBy,
    rank = rank,
    popularity = popularity,
    favorites = favorites,
    synopsis = synopsis,
    background = background,
    season = season,
    year = year,
    genres = genres
)

fun AnimeEntity.asFtsEntity() = AnimeFtsEntity(
    animeId = id,
    title = title,
    synopsis = synopsis
)