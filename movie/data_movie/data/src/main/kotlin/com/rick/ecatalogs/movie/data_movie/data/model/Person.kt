package com.rick.ecatalogs.movie.data_movie.data.model

import com.rick.ecatalogs.movie.data_movie.model.tmdb.person.MediaType
import com.rick.ecatalogs.movie.data_movie.model.tmdb.person.Person
import com.rick.ecatalogs.movie.data_movie.model.tmdb.person.PersonCredit
import com.rick.ecatalogs.movie.data_movie.network.model.PersonCreditResponse
import com.rick.ecatalogs.movie.data_movie.network.model.PersonResponse

/** How many credits the actor screen shows. */
internal const val MAX_PERSON_CREDITS = 30

fun PersonResponse.asPerson() = Person(
    id = id,
    name = name,
    biography = biography.orEmpty().trim(),
    birthday = birthday?.takeIf { it.isNotBlank() },
    deathday = deathday?.takeIf { it.isNotBlank() },
    placeOfBirth = placeOfBirth?.takeIf { it.isNotBlank() },
    knownForDepartment = knownForDepartment?.takeIf { it.isNotBlank() },
    profilePath = profilePath?.takeIf { it.isNotBlank() },
    credits = combinedCredits?.cast.orEmpty().asPersonCredits(),
)

/**
 * Movies and series only (no talk-show style entries without a poster), one entry per title
 * (a series can be listed once per character), best known first by number of votes.
 */
internal fun List<PersonCreditResponse>.asPersonCredits(): List<PersonCredit> =
    sortedByDescending { it.voteCount ?: 0 }
        .mapNotNull { it.asPersonCredit() }
        .filter { it.posterPath != null }
        .distinctBy { it.mediaType to it.id }
        .take(MAX_PERSON_CREDITS)

private fun PersonCreditResponse.asPersonCredit(): PersonCredit? {
    val type = when (mediaType) {
        "movie" -> MediaType.MOVIE
        "tv" -> MediaType.SERIES
        else -> return null
    }
    return PersonCredit(
        id = id,
        mediaType = type,
        title = (title ?: name).orEmpty(),
        character = character.orEmpty().trim(),
        posterPath = posterPath?.takeIf { it.isNotBlank() },
        year = (releaseDate ?: firstAirDate)?.take(4)?.takeIf { it.length == 4 },
        voteAverage = voteAverage ?: 0.0,
    )
}
