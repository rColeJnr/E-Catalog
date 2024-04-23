package com.rick.data.model_movie.tmdb.series


import com.rick.data.model_movie.tmdb.movie.Genre

data class SeriesResponse(
    val id: Int,
    val adult: Boolean,
    val backdropImage: String,
    val createdBy: List<Creator>,
    val firstAirDate: String,
    val genres: List<Genre>,
    val homepage: String,
    val inProduction: Boolean,
    val lastAirDate: String,
    val name: String,
    val numberOfSeasons: Int,
    val summary: String,
    val popularity: Double,
    val image: String,
    val voteAverage: Double,
    val voteCount: Int,
    val similar: SeriesSimilarResponse
)