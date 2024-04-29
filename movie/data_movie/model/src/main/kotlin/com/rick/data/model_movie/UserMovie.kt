package com.rick.data.model_movie

import com.rick.data.model_movie.tmdb.movie.Genre
import com.rick.data.model_movie.tmdb.movie.MyMovie
import com.rick.data.model_movie.tmdb.trending_movie.TrendingMovie

data class UserMovie internal constructor(
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
    val similar: List<TrendingMovie>,
    val isFavorite: Boolean
) {

    constructor(movie: MyMovie, userData: TrendingMovieUserData) : this(
        id = movie.id,
        adult = movie.adult,
        budget = movie.budget,
        genres = movie.genres,
        homepage = movie.homepage,
        overview = movie.overview,
        popularity = movie.popularity,
        image = movie.image,
        releaseDate = movie.releaseDate,
        revenue = movie.revenue,
        runtime = movie.runtime,
        title = movie.title,
        voteAverage = movie.voteAverage,
        voteCount = movie.voteCount,
        similar = movie.similar,
        isFavorite = movie.id in userData.trendingMovieFavoriteIds
    )
}