package com.rick.ecatalogs.movie.data_movie.data.model

import com.rick.ecatalogs.movie.data_movie.model.tmdb.person.MediaType
import com.rick.ecatalogs.movie.data_movie.network.model.CombinedCreditsResponse
import com.rick.ecatalogs.movie.data_movie.network.model.PersonCreditResponse
import com.rick.ecatalogs.movie.data_movie.network.model.PersonResponse
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PersonTest {

    @Test
    fun credits_areSortedByVotes_withoutPosterlessOrUnknownMedia() {
        val credits = listOf(
            credit(1, "movie", votes = 10),
            credit(2, "tv", votes = 500, title = null, name = "Show", firstAirDate = "2019-06-16"),
            credit(3, "movie", votes = 9_000, poster = null),
            credit(4, "person", votes = 99_999),
            credit(5, "movie", votes = 100, releaseDate = "2024-02-27"),
        ).asPersonCredits()

        assertEquals(listOf(2, 5, 1), credits.map { it.id })
        assertEquals(MediaType.SERIES, credits[0].mediaType)
        assertEquals("Show", credits[0].title)
        assertEquals("2019", credits[0].year)
        assertEquals("2024", credits[1].year)
    }

    @Test
    fun credits_listATitleOnce_evenWithSeveralCharacters() {
        val credits = listOf(
            credit(7, "tv", votes = 50, character = "Rue"),
            credit(7, "tv", votes = 50, character = "Narrator"),
            credit(7, "movie", votes = 10),
        ).asPersonCredits()

        assertEquals(2, credits.size)
        assertEquals("Rue", credits.first { it.mediaType == MediaType.SERIES }.character)
    }

    @Test
    fun credits_areCapped() {
        val credits = (1..MAX_PERSON_CREDITS + 10).map { credit(it, "movie", votes = it) }
            .asPersonCredits()
        assertEquals(MAX_PERSON_CREDITS, credits.size)
    }

    @Test
    fun blankOptionalFields_becomeNull() {
        val person = PersonResponse(
            id = 1, name = "Someone", biography = null, birthday = "", placeOfBirth = " ",
            profilePath = "", combinedCredits = CombinedCreditsResponse(cast = null),
        ).asPerson()

        assertEquals("", person.biography)
        assertNull(person.birthday)
        assertNull(person.placeOfBirth)
        assertNull(person.profilePath)
        assertEquals(emptyList(), person.credits)
    }

    private fun credit(
        id: Int,
        mediaType: String,
        votes: Int,
        poster: String? = "/p.jpg",
        title: String? = "Title $id",
        name: String? = null,
        character: String? = "Role",
        releaseDate: String? = null,
        firstAirDate: String? = null,
    ) = PersonCreditResponse(
        id = id, mediaType = mediaType, title = title, name = name, character = character,
        posterPath = poster, releaseDate = releaseDate, firstAirDate = firstAirDate,
        voteCount = votes,
    )
}
