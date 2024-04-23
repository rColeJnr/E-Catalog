package com.rick.data.model_movie

import com.rick.data.model_movie.tmdb.trending_movie.TrendingMovie

data class UserTrendingMovie internal constructor(
    val id: Int,
    val adult: Boolean,
    val image: String,
    val title: String,
    val overview: String,
    val mediaType: String,
    val popularity: Double,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val isFavorite: Boolean,
) {


    constructor(movie: TrendingMovie, userData: TrendingMovieUserData) : this(
        id = movie.id,
        adult = movie.adult,
        title = movie.title,
        overview = movie.overview,
        image = movie.image,
        mediaType = movie.mediaType,
        popularity = movie.popularity,
        releaseDate = movie.releaseDate,
        voteAverage = movie.voteAverage,
        voteCount = movie.voteCount,
        isFavorite = movie.id in userData.trendingMovieFavoriteIds
    )
}

fun List<TrendingMovie>.mapToUserTrendingMovie(userData: TrendingMovieUserData): List<UserTrendingMovie> =
    map { UserTrendingMovie(it, userData) }

fun TrendingMovie.mapToUserTrendingMovie(userData: TrendingMovieUserData): UserTrendingMovie =
    UserTrendingMovie(this, userData)