package com.rick.data.model_anime

import com.rick.data.model_anime.model_jikan.Aired
import com.rick.data.model_anime.model_jikan.Genre
import com.rick.data.model_anime.model_jikan.Trailer

data class Anime(
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
)