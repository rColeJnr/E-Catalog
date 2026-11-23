package com.rick.ecatalogs.anime.data_anime.data.model

import com.rick.ecatalogs.anime.data_anime.database.model.MangaEntity
import com.rick.ecatalogs.anime.data_anime.database.model.MangaRecentSearchQueryEntity
import com.rick.ecatalogs.anime.data_anime.model.MangaRecentSearchQuery
import com.rick.ecatalogs.anime.data_anime.network.model.MangaNetwork

fun MangaNetwork.asEntity(): MangaEntity = MangaEntity(
    id = id,
    url = url,
    image = images?.jpg?.image_url ?: "",
    title = title,
    titleEnglish = titleEnglish ?: "",
    type = type,
    chapters = chapters ?: 0,
    volumes = volumes ?: 0,
    publishing = publishing,
    published = published,
    score = score,
    scoredBy = scoredBy,
    rank = rank,
    popularity = popularity,
    favorites = favorites,
    synopsis = synopsis ?: "",
    background = background ?: "",
    authors = authors,
    serializations = serializations,
    genres = genres,
    themes = themes
)

fun MangaRecentSearchQueryEntity.asExternalModel() = MangaRecentSearchQuery(
    query = query,
    queriedDate = queriedDate,
)