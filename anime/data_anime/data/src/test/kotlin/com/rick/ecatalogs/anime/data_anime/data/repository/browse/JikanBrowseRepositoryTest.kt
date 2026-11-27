package com.rick.ecatalogs.anime.data_anime.data.repository.browse

import com.google.gson.Gson
import com.rick.ecatalogs.anime.data_anime.data.testing.testContext
import com.rick.ecatalogs.anime.data_anime.data.testing.testListCache
import com.rick.ecatalogs.anime.data_anime.data.testing.unimplemented
import com.rick.ecatalogs.anime.data_anime.model.browse.AnimeCharacter
import com.rick.ecatalogs.anime.data_anime.model.browse.Broadcast
import com.rick.ecatalogs.anime.data_anime.model.browse.JikanItem
import com.rick.ecatalogs.anime.data_anime.model.browse.JikanRelation
import com.rick.ecatalogs.anime.data_anime.model.browse.MangaList
import com.rick.ecatalogs.anime.data_anime.model.browse.VoiceActor
import com.rick.ecatalogs.anime.data_anime.network.JikanNetworkDataSource
import com.rick.ecatalogs.anime.data_anime.network.model.AnimeFullResponse
import com.rick.ecatalogs.anime.data_anime.network.model.AnimeResponse
import com.rick.ecatalogs.anime.data_anime.network.model.JikanCharactersResponse
import com.rick.ecatalogs.anime.data_anime.network.model.JikanEpisodesResponse
import com.rick.ecatalogs.anime.data_anime.network.model.JikanPersonResponse
import com.rick.ecatalogs.anime.data_anime.network.model.JikanRecommendationsResponse
import com.rick.ecatalogs.anime.data_anime.network.model.MangaFullResponse
import com.rick.ecatalogs.anime.data_anime.network.model.MangaResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The repository's mapping of jikan-edge responses. Responses are parsed from jikan-edge-shaped JSON
 * with Gson, as Retrofit does, so a model field that doesn't match the JSON fails here too.
 */
class JikanBrowseRepositoryTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private val gson = Gson()
    private lateinit var network: FakeJikanNetwork
    private lateinit var subject: JikanBrowseRepositoryImpl

    @Before
    fun setUp() {
        network = FakeJikanNetwork()
        subject = JikanBrowseRepositoryImpl(testContext(tmp.root), network, testListCache())
    }

    @Test
    fun relations_putAdaptationsFirst_andKeepOnlyAnimeAndManga() = runTest {
        network.animeFull = """
            {"data": {"malId": 1, "title": "Frieren", "relations": [
              {"relation": "Sequel", "malId": 2, "type": "anime", "title": "Frieren 2"},
              {"relation": "Adaptation", "malId": 3, "type": "manga", "title": "Frieren (manga)"},
              {"relation": "Other", "malId": 4, "type": "music", "title": "OST"},
              {"relation": "", "malId": 5, "type": "anime", "title": "No label"}
            ]}, "meta": {"stale": false}}
        """

        assertEquals(
            listOf(
                JikanRelation("Adaptation", 3, isAnime = false, name = "Frieren (manga)"),
                JikanRelation("Sequel", 2, isAnime = true, name = "Frieren 2"),
            ),
            subject.animeExtras(1).relations,
        )
    }

    @Test
    fun animeExtras_readBroadcastAndStreaming_defaultingToJapanTime() = runTest {
        network.animeFull = """
            {"data": {"malId": 1, "title": "Frieren",
              "trailer": {"youtubeId": "abc123XYZ", "embedUrl": null},
              "broadcast": {"day": "Fridays", "time": "23:00", "timezone": ""},
              "streaming": [{"name": "Crunchyroll", "url": "https://cr.example"}, {"name": "", "url": "x"}]
            }}
        """

        val extras = subject.animeExtras(1)

        assertEquals("abc123XYZ", extras.trailerYoutubeId)
        assertEquals(Broadcast("Fridays", "23:00", "Asia/Tokyo"), extras.broadcast)
        assertEquals(listOf("Crunchyroll"), extras.streaming.map { it.name })
        assertEquals(emptyList(), extras.relations)
    }

    @Test
    fun animeExtras_readJstAsJapanTime() = runTest {
        network.animeFull = """
            {"data": {"malId": 1, "broadcast": {"day": "Fridays", "time": "23:00", "timezone": "JST"}}}
        """

        assertEquals(Broadcast("Fridays", "23:00", "Asia/Tokyo"), subject.animeExtras(1).broadcast)
    }

    @Test
    fun mangaRelations_areRememberedForTheSession() = runTest {
        network.mangaFull = """
            {"data": {"malId": 9, "relations": [
              {"relation": "Adaptation", "malId": 1, "type": "anime", "title": "Frieren"}
            ]}}
        """

        val first = subject.mangaRelations(9)
        val second = subject.mangaRelations(9)

        assertEquals(listOf(JikanRelation("Adaptation", 1, isAnime = true, name = "Frieren")), first)
        assertEquals(first, second)
        assertEquals(1, network.calls.count { it == "mangaFull:9" })
    }

    @Test
    fun staleResponses_areShownButNotRemembered() = runTest {
        network.mangaFull = """
            {"data": {"malId": 9, "relations": [
              {"relation": "Adaptation", "malId": 1, "type": "anime", "title": "Frieren"}
            ]}, "meta": {"stale": true}}
        """

        val first = subject.mangaRelations(9)
        subject.mangaRelations(9)

        assertEquals(listOf(JikanRelation("Adaptation", 1, isAnime = true, name = "Frieren")), first)
        assertEquals(2, network.calls.count { it == "mangaFull:9" })
    }

    @Test
    fun characters_haveNamesAndJapaneseVoiceActors_mainFirst() = runTest {
        network.characters = """
            {"data": [
              {"malId": 20, "name": "Fern", "imageUrl": "fern.jpg", "role": "Supporting",
               "voiceActors": [{"malId": 200, "name": "Ichinose, Kana", "language": "Japanese"}]},
              {"malId": 10, "name": "Frieren", "role": "Main",
               "voiceActors": [
                 {"malId": 101, "name": "Mallory Low", "language": "English"},
                 {"malId": 100, "name": "Tanezaki, Atsumi", "language": "Japanese"}
               ]},
              {"role": "Main"}
            ]}
        """

        assertEquals(
            listOf(
                AnimeCharacter(10, "Frieren", null, isMain = true, VoiceActor(100, "Tanezaki, Atsumi", null)),
                AnimeCharacter(20, "Fern", "fern.jpg", isMain = false, VoiceActor(200, "Ichinose, Kana", null)),
            ),
            subject.animeCharacters(1),
        )
    }

    @Test
    fun person_mapsNameBirthdayAndRoles_mainRolesFirst_oncePerAnime() = runTest {
        network.person = """
            {"data": {"malId": 100, "name": "Atsumi Tanezaki", "familyName": "種﨑", "givenName": "敦美",
              "birthday": "Sep 27, 1990", "birthDate": "1990-09-27", "about": "  Voice actress.  ",
              "voices": [
                {"animeId": 1, "animeTitle": "Spy x Family", "characterName": "Anya", "characterRole": "Supporting"},
                {"animeId": 2, "animeTitle": "Frieren", "animeImageUrl": "frieren.jpg",
                 "characterName": "Frieren", "characterRole": "Main"},
                {"animeId": 2, "animeTitle": "Frieren", "characterName": "Narrator", "characterRole": "Supporting"},
                {"characterName": "No anime", "characterRole": "Main"}
              ]}}
        """

        val person = subject.person(100)

        assertEquals("Atsumi Tanezaki", person.name)
        assertEquals("種﨑敦美", person.nativeName)
        assertEquals("1990-09-27", person.birthday)
        assertEquals("Voice actress.", person.about)
        assertEquals(listOf(2, 1), person.roles.map { it.animeId })
        assertEquals("Frieren", person.roles.first().characterName)
        assertEquals("frieren.jpg", person.roles.first().animeImageUrl)
    }

    @Test
    fun person_withoutNativeNameOrBirthday_givesNulls() = runTest {
        network.person = """{"data": {"malId": 5, "name": "Someone", "familyName": " ", "birthDate": null}}"""

        val person = subject.person(5)

        assertNull(person.nativeName)
        assertNull(person.birthday)
        assertEquals(emptyList(), person.roles)
    }

    @Test
    fun schedule_isOneRequestPerDay_withoutDuplicates() = runTest {
        network.schedule = """
            {"data": [
              {"malId": 1, "title": "Ichi", "imageUrl": "1.jpg", "score": 8.1},
              {"malId": 2, "title": "Ni", "score": 0},
              {"malId": 1, "title": "Ichi"}
            ], "meta": {"stale": false}}
        """

        val shows = subject.schedule("monday")

        assertEquals(listOf(1, 2), shows.map { it.id })
        assertEquals(listOf("Ichi", "Ni"), shows.map { it.title })
        assertEquals(listOf(8.1, null), shows.map { it.score })
        assertEquals("1.jpg", shows.first().imageUrl)
        assertNull(shows.first().broadcast)
        assertEquals(listOf("schedule:monday"), network.calls)
    }

    @Test
    fun episodes_convertMalDatesToIso() = runTest {
        network.episodes = """
            {"data": [
              {"number": 1, "title": "The Journey's End", "aired": "Sep 29, 2023", "score": 4.28, "filler": false},
              {"number": 2, "title": "Not aired", "aired": null, "score": 0}
            ], "meta": {"pagination": {"page": 1, "hasNextPage": false}}}
        """

        val episodes = subject.animeEpisodes(1)

        assertEquals(listOf("2023-09-29", null), episodes.map { it.airDate })
        assertEquals(listOf(4.28, null), episodes.map { it.score })
    }

    @Test
    fun recommendations_areFlatEntries_mostVotedFirst() = runTest {
        network.recommendations = """
            {"data": [
              {"malId": 2, "title": "Mushishi", "imageUrl": "m.jpg", "votes": 3},
              {"malId": 3, "title": "Violet Evergarden", "imageUrl": "v.jpg", "votes": 31}
            ]}
        """

        assertEquals(
            listOf(JikanItem(3, "Violet Evergarden", "v.jpg"), JikanItem(2, "Mushishi", "m.jpg")),
            subject.animeRecommendations(1),
        )
    }

    @Test
    fun randomPicks_areNotRemembered() = runTest {
        network.randomAnimeIds = ArrayDeque(listOf(7, 8))

        assertEquals(7, subject.randomAnimeId())
        assertEquals(8, subject.randomAnimeId())
    }

    @Test
    fun randomPicks_skipAdultTitles() = runTest {
        network.randomAnimeIds = ArrayDeque(listOf(7, 8))
        network.adultRandomIds = setOf(7)

        assertEquals(8, subject.randomAnimeId())
    }

    @Test
    fun featuredManga_skipsIncompleteEntries_andIsReusedWhileFresh() = runTest {
        network.mangaPage = """
            {"meta": {"pagination": {"hasNextPage": true}},
             "data": [
               {"malId": 1, "url": "u1", "title": "Sousou no Frieren", "type": "Manga", "imageUrl": "f.jpg"},
               {"malId": 2, "url": "u2", "title": "No type"},
               {"malId": 1, "url": "u1", "title": "Duplicate", "type": "Manga"}
             ]}
        """

        val first = subject.featuredManga(MangaList.PUBLISHING).first()
        network.mangaPage = """{"meta": {"pagination": {"hasNextPage": false}}, "data": []}"""
        val second = subject.featuredManga(MangaList.PUBLISHING).first()

        assertEquals(listOf(JikanItem(1, "Sousou no Frieren", "f.jpg")), first)
        assertEquals(first, second)
        assertEquals(listOf("browseManga:members:publishing"), network.calls)
    }

    @Test
    fun featuredFavoriteManga_comesFromTheTopFavoritesList() = runTest {
        network.mangaPage = """{"data": [{"malId": 1, "title": "Berserk", "type": "Manga"}]}"""

        subject.featuredManga(MangaList.FAVORITES).first()

        assertEquals(listOf("topManga:favorite"), network.calls)
    }

    @Test
    fun featuredManga_isEmptyWhenOfflineWithNothingStored() = runTest {
        network.mangaPage = null

        assertTrue(subject.featuredManga(MangaList.POPULAR).first().isEmpty())
    }

    /** Serves jikan-edge JSON set by the test; a null response is a network failure. */
    private inner class FakeJikanNetwork : JikanNetworkDataSource by unimplemented() {
        val calls = mutableListOf<String>()
        var animeFull: String? = null
        var mangaFull: String? = null
        var characters: String? = null
        var person: String? = null
        var mangaPage: String? = null
        var schedule: String? = null
        var episodes: String? = null
        var recommendations: String? = null
        var randomAnimeIds = ArrayDeque<Int>()
        var adultRandomIds = emptySet<Int>()

        private inline fun <reified T> parse(json: String?): T =
            gson.fromJson(json ?: throw IOException("offline"), T::class.java)

        override suspend fun fetchAnimeFull(id: Int): AnimeFullResponse =
            parse<AnimeFullResponse>(animeFull).also { calls += "animeFull:$id" }

        override suspend fun fetchMangaFull(id: Int): MangaFullResponse =
            parse<MangaFullResponse>(mangaFull).also { calls += "mangaFull:$id" }

        override suspend fun fetchAnimeCharacters(id: Int): JikanCharactersResponse = parse(characters)

        override suspend fun fetchPerson(id: Int): JikanPersonResponse = parse(person)

        override suspend fun fetchAnimeEpisodes(id: Int, page: Int): JikanEpisodesResponse = parse(episodes)

        override suspend fun fetchAnimeRecommendations(id: Int): JikanRecommendationsResponse =
            parse(recommendations)

        override suspend fun fetchSchedule(day: String): AnimeResponse {
            calls += "schedule:$day"
            return parse(schedule)
        }

        override suspend fun browseManga(
            page: Int,
            orderBy: String,
            sort: String,
            status: String?,
            genres: Int?,
        ): MangaResponse = parse<MangaResponse>(mangaPage).also { calls += "browseManga:$orderBy:$status" }

        override suspend fun fetchTopManga(page: Int, filter: String): MangaResponse =
            parse<MangaResponse>(mangaPage).also { calls += "topManga:$filter" }

        override suspend fun fetchRandomAnime(): AnimeFullResponse {
            val id = randomAnimeIds.removeFirst()
            val rating = if (id in adultRandomIds) "Rx - Hentai" else "PG-13 - Teens 13 or older"
            return parse("""{"data": {"malId": $id, "rating": "$rating"}}""")
        }
    }
}
