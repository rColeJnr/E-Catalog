package com.rick.data.anime_favorite.model

import com.rick.data.database_anime.model.MangaEntity
import com.rick.data.network_anime.model.MangaNetwork

fun MangaNetwork.asEntity(): MangaEntity = MangaEntity(
    id = id,
    url = url,
    image = images.jpg.image_url,
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
    synopsis = synopsis,
    background = background ?: "",
    authors = authors,
    serializations = serializations,
    genres = genres,
    themes = themes
)