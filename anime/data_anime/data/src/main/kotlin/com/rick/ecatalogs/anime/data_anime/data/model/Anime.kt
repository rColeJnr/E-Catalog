package com.rick.ecatalogs.anime.data_anime.data.model

import com.rick.ecatalogs.anime.data_anime.database.model.AnimeEntity
import com.rick.ecatalogs.anime.data_anime.database.model.AnimeRecentSearchQueryEntity
import com.rick.ecatalogs.anime.data_anime.model.AnimeRecentSearchQuery
import com.rick.ecatalogs.anime.data_anime.network.model.AnimeNetwork

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