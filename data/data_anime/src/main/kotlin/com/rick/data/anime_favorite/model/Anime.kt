package com.rick.data.anime_favorite.model

import com.rick.data.database_anime.model.AnimeEntity
import com.rick.data.network_anime.model.AnimeNetwork

fun AnimeNetwork.asEntity(): AnimeEntity = AnimeEntity(
    id = id,
    url = url,
    images = images.jpg.image_url,
    trailer = trailer,
    title = title,
    type = type,
    source = source,
    episodes = episodes,
    airing = airing,
    aired = aired,
    runtime = duration,
    rating = rating,
    score = score,
    scoredBy = scoredBy,
    rank = rank,
    popularity = popularity,
    favorites = favorites,
    synopsis = synopsis,
    background = background ?: "",
    season = season ?: "",
    year = year ?: 0,
    genres = genres
)