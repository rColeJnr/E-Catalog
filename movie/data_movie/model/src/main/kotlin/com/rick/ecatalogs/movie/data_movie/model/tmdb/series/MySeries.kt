package com.rick.ecatalogs.movie.data_movie.model.tmdb.series

import com.rick.ecatalogs.movie.data_movie.model.tmdb.movie.Genre
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_series.TrendingSeries

data class MySeries(
    val id: Int,
    val adult: Boolean,
    val createdBy: List<Creator>,
    val episodeRuntime: List<Int>,
    val firstAirDate: String,
    val genres: List<Genre>,
    val homepage: String,
    val inProduction: Boolean,
    val lastAirDate: String?,
    val name: String,
    val numberOfSeasons: Int,
    val numberOfEpisodes: Int,
    val overview: String,
    val popularity: Double,
    val image: String,
    val voteAverage: Double,
    val similar: List<TrendingSeries>
)
