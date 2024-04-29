package com.rick.data.model_movie.tmdb.series


data class LastEpisodeToAir(
    val id: Int,
    val airdate: String,
    val episodeNumber: Int,
    val name: String,
    val overview: String,
    val seasonNumber: Int,
    val stillPath: String,
    val voteAverage: Double,
    val voteCount: Int
)