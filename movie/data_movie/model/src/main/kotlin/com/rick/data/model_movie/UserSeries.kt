package com.rick.data.model_movie

import com.rick.data.model_movie.tmdb.movie.Genre
import com.rick.data.model_movie.tmdb.series.Creator
import com.rick.data.model_movie.tmdb.series.MySeries
import com.rick.data.model_movie.tmdb.trending_series.TrendingSeries

data class UserSeries internal constructor(
    val id: Int,
    val adult: Boolean,
    val createdBy: List<Creator>,
    val firstAirDate: String,
    val genres: List<Genre>,
    val homepage: String,
    val inProduction: Boolean,
    val lastAirDate: String,
    val name: String,
    val numberOfSeasons: Int,
    val overview: String,
    val popularity: Double,
    val image: String,
    val voteAverage: Double,
    val similar: List<TrendingSeries>,
    val isFavorite: Boolean
) {

    constructor(series: MySeries, userData: TrendingSeriesUserData) : this(
        id = series.id,
        adult = series.adult,
        createdBy = series.createdBy,
        firstAirDate = series.firstAirDate,
        genres = series.genres,
        homepage = series.homepage,
        inProduction = series.inProduction,
        lastAirDate = series.lastAirDate ?: "",
        name = series.name,
        numberOfSeasons = series.numberOfSeasons,
        overview = series.overview,
        popularity = series.popularity,
        image = series.image,
        voteAverage = series.voteAverage,
        similar = series.similar,
        isFavorite = series.id in userData.trendingSeriesFavoriteIds
    )
}
