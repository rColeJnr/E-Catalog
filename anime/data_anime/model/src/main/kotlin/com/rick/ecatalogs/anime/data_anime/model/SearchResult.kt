package com.rick.ecatalogs.anime.data_anime.model

data class AnimeSearchResult(
    val result: List<Anime> = emptyList()
)

data class MangaSearchResult(
    val result: List<Manga> = emptyList()
)
