package com.rick.ecatalogs.book.data_book.model.gutenberg

import com.google.gson.Gson
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

/** Gutendex is parsed with Gson in prod and with kotlinx in the demo flavor: both read the years. */
class AuthorTest {

    private val json = """{"name": "Shelley, Mary Wollstonecraft", "birth_year": 1797, "death_year": 1851}"""

    @Test
    fun gson_readsBirthAndDeathYears() {
        val author = Gson().fromJson(json, Author::class.java)

        assertEquals(1797, author.birthYear)
        assertEquals(1851, author.deathYear)
    }

    @Test
    fun kotlinxSerialization_readsBirthAndDeathYears() {
        val author = Json.decodeFromString(Author.serializer(), json)

        assertEquals(1797, author.birthYear)
        assertEquals(1851, author.deathYear)
    }

    @Test
    fun unknownYears_areNull() {
        val author = Gson().fromJson("""{"name": "Anonymous", "birth_year": null}""", Author::class.java)

        assertEquals(null, author.birthYear)
        assertEquals(null, author.deathYear)
    }
}
