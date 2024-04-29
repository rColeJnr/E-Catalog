package com.rick.data.model_anime

data class AnimeSearchResult(
    val result: List<Anime> = emptyList()
)

data class MangaSearchResult(
    val result: List<Manga> = emptyList()
)
