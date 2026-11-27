package com.rick.ecatalogs.movie.data_movie.data.model

import com.rick.ecatalogs.movie.data_movie.model.tmdb.movie.CastMember
import com.rick.ecatalogs.movie.data_movie.model.tmdb.movie.TmdbDetailsExtras
import com.rick.ecatalogs.movie.data_movie.model.tmdb.movie.WatchProvider
import com.rick.ecatalogs.movie.data_movie.model.tmdb.movie.WatchProviders
import com.rick.ecatalogs.movie.data_movie.network.model.ContentRatingsResponse
import com.rick.ecatalogs.movie.data_movie.network.model.ImageResponse
import com.rick.ecatalogs.movie.data_movie.network.model.ImagesResponse
import com.rick.ecatalogs.movie.data_movie.network.model.CreditsResponse
import com.rick.ecatalogs.movie.data_movie.network.model.ReleaseDatesResponse
import com.rick.ecatalogs.movie.data_movie.network.model.VideoResponse
import com.rick.ecatalogs.movie.data_movie.network.model.VideosResponse
import com.rick.ecatalogs.movie.data_movie.network.model.WatchProviderResponse
import com.rick.ecatalogs.movie.data_movie.network.model.WatchProvidersResponse

/** How many cast members to keep (TMDB returns the whole cast, ordered by billing). */
internal const val MAX_CAST = 15

/** How many backdrops the details gallery shows after the trailer and the poster. */
internal const val MAX_BACKDROPS = 6

internal fun tmdbDetailsExtras(
    credits: CreditsResponse?,
    videos: VideosResponse?,
    images: ImagesResponse?,
    watchProviders: WatchProvidersResponse?,
    ageRatings: Map<String, String>,
) = TmdbDetailsExtras(
    cast = credits?.cast.orEmpty()
        .sortedBy { it.order ?: Int.MAX_VALUE }
        .take(MAX_CAST)
        .map { CastMember(it.id, it.name, it.character.orEmpty(), it.profilePath) },
    trailerYoutubeKey = videos?.results.orEmpty().bestTrailer()?.key,
    backdrops = images?.backdrops.orEmpty().bestBackdrops(),
    watchProviders = watchProviders?.results.orEmpty().mapValues { (_, country) ->
        WatchProviders(
            link = country.link,
            stream = country.flatrate.asWatchProviders(),
            rent = country.rent.asWatchProviders(),
            buy = country.buy.asWatchProviders(),
        )
    },
    ageRatings = ageRatings,
)

/** Official trailer, then any trailer, then a teaser; YouTube only (the app opens YouTube). */
internal fun List<VideoResponse>.bestTrailer(): VideoResponse? {
    val youtube = filter { it.site.equals("YouTube", ignoreCase = true) && it.key.isNotBlank() }
    return youtube.firstOrNull { it.type == "Trailer" && it.official == true }
        ?: youtube.firstOrNull { it.type == "Trailer" }
        ?: youtube.firstOrNull { it.type == "Teaser" }
}

/**
 * Textless backdrops first (no title baked in), then English; best voted first; no duplicates.
 * The request asks for `en,null` only (IMAGE_LANGUAGES).
 */
internal fun List<ImageResponse>.bestBackdrops(): List<String> =
    filter { !it.filePath.isNullOrBlank() }
        .sortedWith(compareBy<ImageResponse> { it.language != null }.thenByDescending { it.voteAverage ?: 0.0 })
        .mapNotNull { it.filePath }
        .distinct()
        .take(MAX_BACKDROPS)

/** Movies: per country, the theatrical certification if there is one, else the first given. */
internal fun ReleaseDatesResponse?.asAgeRatings(): Map<String, String> =
    this?.results.orEmpty().mapNotNull { country ->
        val certified = country.releaseDates.orEmpty().filter { !it.certification.isNullOrBlank() }
        val rating = (certified.firstOrNull { it.type == THEATRICAL } ?: certified.firstOrNull())
            ?.certification
        rating?.let { country.country to it.trim() }
    }.toMap()

/** Series: one rating per country. */
internal fun ContentRatingsResponse?.asAgeRatings(): Map<String, String> =
    this?.results.orEmpty()
        .filter { !it.rating.isNullOrBlank() }
        .associate { it.country to it.rating!!.trim() }

private fun List<WatchProviderResponse>?.asWatchProviders() = orEmpty()
    .sortedBy { it.displayPriority ?: Int.MAX_VALUE }
    .map { WatchProvider(it.providerId, it.providerName, it.logoPath) }

private const val THEATRICAL = 3
