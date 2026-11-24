package com.rick.ecatalogs.movie.data_movie.model.tmdb.person

data class Person(
    val id: Int,
    val name: String,
    val biography: String,
    /** yyyy-MM-dd, or null when unknown. */
    val birthday: String?,
    val deathday: String?,
    val placeOfBirth: String?,
    /** e.g. "Acting", "Directing". */
    val knownForDepartment: String?,
    val profilePath: String?,
    /** Movies and series they acted in, most-voted (best known) first. */
    val credits: List<PersonCredit>,
)

data class PersonCredit(
    val id: Int,
    val mediaType: MediaType,
    val title: String,
    val character: String,
    val posterPath: String?,
    /** Release / first-air year, or null when unknown. */
    val year: String?,
    val voteAverage: Double,
)

enum class MediaType { MOVIE, SERIES }
