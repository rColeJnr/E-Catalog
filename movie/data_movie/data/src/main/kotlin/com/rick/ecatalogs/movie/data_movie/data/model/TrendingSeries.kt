package com.rick.ecatalogs.movie.data_movie.data.model

import com.rick.ecatalogs.movie.data_movie.model.tmdb.browse.Season
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingSeriesEntity
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingSeriesRecentSearchQueryEntity
import com.rick.ecatalogs.movie.data_movie.model.TmdbRecentSearchQuery
import com.rick.ecatalogs.movie.data_movie.model.tmdb.series.MySeries
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_series.TrendingSeries
import com.rick.ecatalogs.movie.data_movie.network.model.SeriesResponse
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingSeriesNetwork

fun TrendingSeriesNetwork.asTrendingSeriesEntity() = TrendingSeriesEntity(
    adult = adult,
    id = id,
    name = name.orEmpty(),
    overview = overview.orEmpty(),
    image = image ?: "",
    popularity = popularity ?: 0.0,
    firstAirDate = firstAirDate.orEmpty(),
    voteAverage = voteAverage ?: 0.0,
    voteCount = voteCount ?: 0,
)

fun TrendingSeriesNetwork.asTrendingSeries() = TrendingSeries(
    adult = adult,
    id = id,
    name = name.orEmpty(),
    overview = overview.orEmpty(),
    image = image ?: "",
    popularity = popularity ?: 0.0,
    firstAirDate = firstAirDate.orEmpty(),
    voteAverage = voteAverage ?: 0.0,
    voteCount = voteCount ?: 0,
)

fun SeriesResponse.asMySeries() = MySeries(id,
    adult = adult,
    createdBy = createdBy,
    episodeRuntime = episodeRuntime,
    firstAirDate = firstAirDate,
    genres = genres,
    homepage = homepage,
    inProduction = inProduction,
    lastAirDate = lastAirDate,
    name = name,
    numberOfSeasons = numberOfSeasons,
    numberOfEpisodes = numberOfEpisodes,
    overview = overview,
    popularity = popularity,
    image = image,
    voteAverage = voteAverage,
    similar = similar.results.map { it.asTrendingSeries() },
    extras = tmdbDetailsExtras(
        credits = credits,
        videos = videos,
        watchProviders = watchProviders,
        ageRatings = contentRatings.asAgeRatings(),
    ),
    seasons = seasons.orEmpty()
        .sortedBy { if (it.seasonNumber == 0) Int.MAX_VALUE else it.seasonNumber }
        .map {
            Season(
                number = it.seasonNumber,
                name = it.name.orEmpty(),
                episodeCount = it.episodeCount ?: 0,
                airDate = it.airDate?.takeIf { date -> date.isNotBlank() },
                posterPath = it.posterPath?.takeIf { path -> path.isNotBlank() },
            )
        },
)

fun TrendingSeriesRecentSearchQueryEntity.asExternalModel() = TmdbRecentSearchQuery(
    query = query,
    queriedDate = queriedDate,
)