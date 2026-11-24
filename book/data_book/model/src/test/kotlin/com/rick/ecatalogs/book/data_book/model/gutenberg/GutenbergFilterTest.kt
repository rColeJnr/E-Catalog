package com.rick.ecatalogs.book.data_book.model.gutenberg

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class GutenbergFilterTest {

    @Test
    fun cacheKey_isPerFilterAndSharesThePrefix() {
        assertEquals("gutenberg:all:all", GutenbergFilter().cacheKey)
        assertEquals("gutenberg:pt:poetry", GutenbergFilter("pt", "poetry").cacheKey)
        assertNotEquals(GutenbergFilter("pt").cacheKey, GutenbergFilter(topic = "pt").cacheKey)
    }
}
