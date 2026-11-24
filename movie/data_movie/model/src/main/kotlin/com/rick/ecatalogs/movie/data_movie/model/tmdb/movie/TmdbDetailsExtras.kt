package com.rick.ecatalogs.movie.data_movie.model.tmdb.movie

/**
 * Extra details for a movie or series, all fetched in the same details request.
 * Watch providers and age ratings are kept for every country; the UI picks the user's.
 */
data class TmdbDetailsExtras(
    val cast: List<CastMember> = emptyList(),
    /** YouTube video key of the best trailer (official trailer > trailer > teaser), if any. */
    val trailerYoutubeKey: String? = null,
    /** Keyed by ISO 3166-1 country code. */
    val watchProviders: Map<String, WatchProviders> = emptyMap(),
    /** Keyed by ISO 3166-1 country code, e.g. "US" -> "PG-13". */
    val ageRatings: Map<String, String> = emptyMap(),
)

data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String?,
)

data class WatchProviders(
    /** TMDB's "where to watch" page for this title in the country. */
    val link: String?,
    val stream: List<WatchProvider>,
    val rent: List<WatchProvider>,
    val buy: List<WatchProvider>,
)

data class WatchProvider(
    val id: Int,
    val name: String,
    val logoPath: String?,
)
