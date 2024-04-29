package com.rick.movie.data_movie.data.model

import com.rick.data.database_movie.model.TrendingSeriesEntity
import com.rick.data.database_movie.model.TrendingSeriesRecentSearchQueryEntity
import com.rick.data.model_movie.TmdbRecentSearchQuery
import com.rick.data.model_movie.tmdb.series.MySeries
import com.rick.data.model_movie.tmdb.trending_series.TrendingSeries
import com.rick.data.network_movie.model.SeriesResponse
import com.rick.data.network_movie.model.TrendingSeriesNetwork

fun TrendingSeriesNetwork.asTrendingSeriesEntity() = TrendingSeriesEntity(
    adult = adult,
    id = id,
    name = name,
    overview = overview,
    image = image,
    popularity = popularity,
    firstAirDate = firstAirDate,
    voteAverage = voteAverage,
    voteCount = voteCount
)

fun TrendingSeriesNetwork.asTrendingSeries() = TrendingSeries(
    adult = adult,
    id = id,
    name = name,
    overview = overview,
    image = image,
    popularity = popularity,
    firstAirDate = firstAirDate,
    voteAverage = voteAverage,
    voteCount = voteCount
)

fun SeriesResponse.asMySeries() = MySeries(id,
    adult,
    createdBy,
    episodeRuntime,
    firstAirDate,
    genres,
    homepage,
    inProduction,
    lastAirDate,
    name,
    numberOfSeasons,
    overview,
    popularity,
    image,
    voteAverage,
    similar = similar.results.map { it.asTrendingSeries() })

fun TrendingSeriesRecentSearchQueryEntity.asExternalModel() = TmdbRecentSearchQuery(
    query = query,
    queriedDate = queriedDate,
)