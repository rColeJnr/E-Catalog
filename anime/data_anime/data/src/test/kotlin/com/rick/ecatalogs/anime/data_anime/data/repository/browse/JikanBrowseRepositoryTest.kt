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
import com.rick.ecatalogs.anime.data_anime.network.model.JikanPersonResponse
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
 * The repository's mapping of Jikan responses. Responses are parsed from Jikan-shaped JSON with
 * Gson, as Retrofit does, so a model field that doesn't match Jikan's JSON fails here too.
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
            {"data": {"mal_id": 1, "title": "Frieren", "relations": [
              {"relation": "Sequel", "entry": [{"mal_id": 2, "type": "anime", "name": "Frieren 2"}]},
              {"relation": "Adaptation", "entry": [{"mal_id": 3, "type": "manga", "name": "Frieren (manga)"}]},
              {"relation": "Other", "entry": [{"mal_id": 4, "type": "music", "name": "OST"}]},
              {"relation": "", "entry": [{"mal_id": 5, "type": "anime", "name": "No label"}]}
            ]}}
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
            {"data": {"mal_id": 1, "title": "Frieren",
              "broadcast": {"day": "Fridays", "time": "23:00", "timezone": ""},
              "streaming": [{"name": "Crunchyroll", "url": "https://cr.example"}, {"name": "", "url": "x"}]
            }}
        """

        val extras = subject.animeExtras(1)

        assertEquals(Broadcast("Fridays", "23:00", "Asia/Tokyo"), extras.broadcast)
        assertEquals(listOf("Crunchyroll"), extras.streaming.map { it.name })
        assertEquals(emptyList(), extras.relations)
    }

    @Test
    fun mangaRelations_areRememberedForTheSession() = runTest {
        network.mangaFull = """
            {"data": {"mal_id": 9, "relations": [
              {"relation": "Adaptation", "entry": [{"mal_id": 1, "type": "anime", "name": "Frieren"}]}
            ]}}
        """

        val first = subject.mangaRelations(9)
        val second = subject.mangaRelations(9)

        assertEquals(listOf(JikanRelation("Adaptation", 1, isAnime = true, name = "Frieren")), first)
        assertEquals(first, second)
        assertEquals(1, network.calls.count { it == "mangaFull:9" })
    }

    @Test
    fun characters_haveNamesAndJapaneseVoiceActors_mainFirst() = runTest {
        network.characters = """
            {"data": [
              {"character": {"mal_id": 20, "name": "Fern", "images": {"jpg": {"image_url": "fern.jpg"}}},
               "role": "Supporting",
               "voice_actors": [{"person": {"mal_id": 200, "name": "Ichinose, Kana"}, "language": "Japanese"}]},
              {"character": {"mal_id": 10, "name": "Frieren"},
               "role": "Main",
               "voice_actors": [
                 {"person": {"mal_id": 101, "name": "Mallory Low"}, "language": "English"},
                 {"person": {"mal_id": 100, "name": "Tanezaki, Atsumi"}, "language": "Japanese"}
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
            {"data": {"mal_id": 100, "name": "Atsumi Tanezaki", "family_name": "種﨑", "given_name": "敦美",
              "birthday": "1990-09-27T00:00:00+00:00", "about": "  Voice actress.  ",
              "voices": [
                {"role": "Supporting", "anime": {"mal_id": 1, "title": "Spy x Family"}, "character": {"mal_id": 7, "name": "Anya"}},
                {"role": "Main", "anime": {"mal_id": 2, "title": "Frieren",
                  "images": {"jpg": {"image_url": "small.jpg", "large_image_url": "large.jpg"}}},
                  "character": {"mal_id": 10, "name": "Frieren"}},
                {"role": "Supporting", "anime": {"mal_id": 2, "title": "Frieren"}, "character": {"mal_id": 11, "name": "Narrator"}},
                {"role": "Main", "character": {"mal_id": 12, "name": "No anime"}}
              ]}}
        """

        val person = subject.person(100)

        assertEquals("Atsumi Tanezaki", person.name)
        assertEquals("種﨑敦美", person.nativeName)
        assertEquals("1990-09-27", person.birthday)
        assertEquals("Voice actress.", person.about)
        assertEquals(listOf(2, 1), person.roles.map { it.animeId })
        assertEquals("Frieren", person.roles.first().characterName)
        assertEquals("large.jpg", person.roles.first().animeImageUrl)
    }

    @Test
    fun person_withoutNativeNameOrBirthday_givesNulls() = runTest {
        network.person = """{"data": {"mal_id": 5, "name": "Someone", "family_name": " ", "birthday": "1990"}}"""

        val person = subject.person(5)

        assertNull(person.nativeName)
        assertNull(person.birthday)
        assertEquals(emptyList(), person.roles)
    }

    @Test
    fun schedule_followsPagination_upToThreePages_withoutDuplicates() = runTest {
        network.schedulePages = listOf(
            animePage(hasNextPage = true, """{"mal_id": 1, "title": "Ichi", "title_english": "One", "score": 8.1}"""),
            animePage(hasNextPage = true, """{"mal_id": 2, "title": "Ni", "score": 0.0}""", """{"mal_id": 1, "title": "Ichi"}"""),
            animePage(hasNextPage = true, """{"mal_id": 3, "title": "San"}"""),
            animePage(hasNextPage = false, """{"mal_id": 4, "title": "Yon"}"""),
        )

        val shows = subject.schedule("monday")

        assertEquals(listOf(1, 2, 3), shows.map { it.id })
        assertEquals(listOf("One", "Ni", "San"), shows.map { it.title })
        assertEquals(listOf(8.1, null, null), shows.map { it.score })
        assertEquals(listOf("schedule:monday:1", "schedule:monday:2", "schedule:monday:3"), network.calls)
    }

    @Test
    fun schedule_stopsWhenJikanSaysThereIsNoNextPage() = runTest {
        network.schedulePages = listOf(
            animePage(
                hasNextPage = false,
                """{"mal_id": 1, "title": "Ichi", "broadcast": {"day": "Mondays", "time": "01:30", "timezone": "Asia/Tokyo"}}""",
            ),
            animePage(hasNextPage = false, """{"mal_id": 2, "title": "Ni"}"""),
        )

        val shows = subject.schedule("monday")

        assertEquals(listOf(1), shows.map { it.id })
        assertEquals(Broadcast("Mondays", "01:30", "Asia/Tokyo"), shows.single().broadcast)
    }

    @Test
    fun randomPicks_areNotRemembered() = runTest {
        network.randomAnimeIds = ArrayDeque(listOf(7, 8))

        assertEquals(7, subject.randomAnimeId())
        assertEquals(8, subject.randomAnimeId())
    }

    @Test
    fun featuredManga_skipsIncompleteEntries_andIsReusedWhileFresh() = runTest {
        network.mangaPage = """
            {"pagination": {"has_next_page": true},
             "data": [
               {"mal_id": 1, "url": "u1", "title": "Sousou no Frieren", "title_english": "Frieren", "type": "Manga",
                "published": {}, "images": {"jpg": {"image_url": "f.jpg"}}},
               {"mal_id": 2, "url": "u2", "title": "No type", "published": {}},
               {"mal_id": 1, "url": "u1", "title": "Duplicate", "type": "Manga", "published": {}}
             ]}
        """

        val first = subject.featuredManga(MangaList.PUBLISHING).first()
        network.mangaPage = """{"pagination": {"has_next_page": false}, "data": []}"""
        val second = subject.featuredManga(MangaList.PUBLISHING).first()

        assertEquals(listOf(JikanItem(1, "Frieren", "f.jpg")), first)
        assertEquals(first, second)
        assertEquals(listOf("browseManga:members:publishing"), network.calls)
    }

    @Test
    fun featuredManga_isEmptyWhenOfflineWithNothingStored() = runTest {
        network.mangaPage = null

        assertTrue(subject.featuredManga(MangaList.POPULAR).first().isEmpty())
    }

    private fun animePage(hasNextPage: Boolean, vararg anime: String) =
        """{"pagination": {"has_next_page": $hasNextPage}, "data": [${anime.joinToString()}]}"""

    /** Serves Jikan JSON set by the test; a null response is a network failure. */
    private inner class FakeJikanNetwork : JikanNetworkDataSource by unimplemented() {
        val calls = mutableListOf<String>()
        var animeFull: String? = null
        var mangaFull: String? = null
        var characters: String? = null
        var person: String? = null
        var mangaPage: String? = null
        var schedulePages: List<String> = emptyList()
        var randomAnimeIds = ArrayDeque<Int>()

        private inline fun <reified T> parse(json: String?): T =
            gson.fromJson(json ?: throw IOException("offline"), T::class.java)

        override suspend fun fetchAnimeFull(id: Int): AnimeFullResponse =
            parse<AnimeFullResponse>(animeFull).also { calls += "animeFull:$id" }

        override suspend fun fetchMangaFull(id: Int): MangaFullResponse =
            parse<MangaFullResponse>(mangaFull).also { calls += "mangaFull:$id" }

        override suspend fun fetchAnimeCharacters(id: Int): JikanCharactersResponse = parse(characters)

        override suspend fun fetchPerson(id: Int): JikanPersonResponse = parse(person)

        override suspend fun fetchSchedule(day: String, page: Int): AnimeResponse {
            calls += "schedule:$day:$page"
            return parse(schedulePages[page - 1])
        }

        override suspend fun browseManga(
            page: Int,
            orderBy: String,
            sort: String,
            status: String?,
            genres: Int?,
        ): MangaResponse = parse<MangaResponse>(mangaPage).also { calls += "browseManga:$orderBy:$status" }

        override suspend fun fetchRandomAnime(): AnimeFullResponse =
            parse("""{"data": {"mal_id": ${randomAnimeIds.removeFirst()}}}""")
    }
}
