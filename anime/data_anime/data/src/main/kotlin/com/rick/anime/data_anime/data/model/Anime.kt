package com.rick.anime.data_anime.data.model

import com.rick.data.database_anime.model.AnimeEntity
import com.rick.data.database_anime.model.AnimeRecentSearchQueryEntity
import com.rick.data.model_anime.AnimeRecentSearchQuery
import com.rick.data.network_anime.model.AnimeNetwork

fun AnimeNetwork.asEntity(): AnimeEntity = AnimeEntity(
    id = id,
    url = url ?: "",
    images = images?.jpg?.image_url ?: "",
    trailer = trailer?.embed_url ?: "",
    title = title ?: "",
    type = type ?: "",
    source = source ?: "",
    episodes = episodes ?: 0,
    airing = airing ?: false,
    aired = aired?.string ?: "",
    runtime = duration ?: "",
    rating = rating ?: "",
    score = score ?: 0.0,
    scoredBy = scoredBy ?: 0,
    rank = rank ?: 0,
    popularity = popularity ?: 0,
    favorites = favorites ?: 0,
    synopsis = synopsis ?: "",
    background = background ?: "",
    season = season ?: "",
    year = year ?: 0,
    genres = genres.map { it.name }
)

fun AnimeRecentSearchQueryEntity.asExternalModel() = AnimeRecentSearchQuery(
    query = query,
    queriedDate = queriedDate,
)