package com.rick.data.model_movie

import com.rick.data.model_movie.tmdb.trending_series.TrendingSeries

data class UserTrendingSeries internal constructor(
    val adult: Boolean,
    val id: Int,
    val name: String,
    val overview: String,
    val image: String,
    val mediaType: String,
    val popularity: Double,
    val firstAirDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val isFavorite: Boolean
) {
    constructor(series: TrendingSeries, userData: TrendingSeriesUserData) : this(
        adult = series.adult,
        id = series.id,
        name = series.name,
        overview = series.overview,
        image = series.image,
        mediaType = series.mediaType,
        popularity = series.popularity,
        firstAirDate = series.firstAirDate,
        voteAverage = series.voteAverage,
        voteCount = series.voteCount,
        isFavorite = series.id in userData.trendingSeriesFavoriteIds
    )
}

fun List<TrendingSeries>.mapToUserTrendingSeries(userData: TrendingSeriesUserData): List<UserTrendingSeries> =
    map { UserTrendingSeries(it, userData) }

fun TrendingSeries.mapToUserTrendingSeries(userData: TrendingSeriesUserData): UserTrendingSeries =
    UserTrendingSeries(this, userData)