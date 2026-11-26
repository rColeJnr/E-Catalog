package com.rick.ecatalogs.movie.data_movie.data.repository.browse

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class NormalizedTitleTest {

    @Test
    fun ignoresCasePunctuationAndAccents() {
        assertEquals(normalizedTitle("THE LET THEM THEORY"), normalizedTitle("The Let Them Theory"))
        assertEquals(normalizedTitle("Amélie"), normalizedTitle("Amelie"))
        assertEquals(normalizedTitle("Wicked: For Good"), normalizedTitle("Wicked For Good"))
    }

    @Test
    fun keepsDifferentTitlesApart() {
        assertNotEquals(normalizedTitle("It"), normalizedTitle("It Ends with Us"))
    }

    @Test
    fun keepsNonLatinLetters() {
        assertEquals("войнаимир", normalizedTitle("Война и мир"))
        assertNotEquals(normalizedTitle("Война"), normalizedTitle("Воина"))
    }
}
