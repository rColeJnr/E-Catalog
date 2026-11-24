package com.rick.ecatalogs.book.data_book.model.gutenberg

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FormatsTest {

    @Test
    fun readableUrl_prefersHtml() {
        val formats = Formats(
            textHtml = "https://www.gutenberg.org/ebooks/84.html.images",
            textPlainUtf8 = "https://www.gutenberg.org/ebooks/84.txt.utf-8",
        )
        assertEquals("https://www.gutenberg.org/ebooks/84.html.images", formats.readableUrl)
    }

    @Test
    fun readableUrl_fallsBackToPlainText() {
        val formats = Formats(textHtml = " ", textPlainAscii = "https://example.org/book.txt")
        assertEquals("https://example.org/book.txt", formats.readableUrl)
    }

    @Test
    fun readableUrl_neverOpensMetadataOrEpub() {
        val formats = Formats(
            rdf = "https://www.gutenberg.org/ebooks/84.rdf",
            epub = "https://www.gutenberg.org/ebooks/84.epub3.images",
            imageJpeg = "https://www.gutenberg.org/cache/epub/84/pg84.cover.medium.jpg",
        )
        assertNull(formats.readableUrl)
    }
}
