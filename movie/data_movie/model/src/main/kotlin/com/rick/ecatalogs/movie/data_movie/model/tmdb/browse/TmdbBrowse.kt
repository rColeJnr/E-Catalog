package com.rick.ecatalogs.movie.data_movie.model.tmdb.browse

/** A TMDB genre, with its name in the user's language. */
data class TmdbGenre(
    val id: Int,
    val name: String,
)

/** A season as listed on the series details screen. */
data class Season(
    val number: Int,
    val name: String,
    val episodeCount: Int,
    /** yyyy-MM-dd, or null when not announced. */
    val airDate: String?,
    val posterPath: String?,
)

/** A season with its episodes. */
data class SeasonDetails(
    val number: Int,
    val name: String,
    val overview: String,
    val episodes: List<Episode>,
)

data class Episode(
    val id: Int,
    val number: Int,
    val name: String,
    val overview: String,
    /** yyyy-MM-dd, or null when not announced. */
    val airDate: String?,
    val stillPath: String?,
    /** Minutes, or null when unknown. */
    val runtime: Int?,
    val voteAverage: Double,
)
