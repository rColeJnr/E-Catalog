package com.rick.data.model_movie.tmdb.movie

import com.rick.data.model_movie.tmdb.trending_movie.TrendingMovie

data class MyMovie(
    val id: Int,
    val adult: Boolean,
    val budget: Int,
    val genres: List<Genre>,
    val homepage: String,
    val overview: String,
    val popularity: Double,
    val image: String,
    val releaseDate: String,
    val revenue: Long,
    val runtime: Int,
    val title: String,
    val voteAverage: Double,
    val voteCount: Int,
    val similar: List<TrendingMovie>
)