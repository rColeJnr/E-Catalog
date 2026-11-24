package com.rick.ecatalogs.book.data_book.model.gutenberg

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Download links Gutendex lists for a book, keyed by MIME type. The JSON keys are unchanged, so
 * books already stored in Room keep their links. Every field defaults to null because books only
 * list some formats (and Gson leaves missing keys null anyway).
 */
@Serializable
data class Formats(
    @SerializedName("image/jpeg")
    @SerialName("image/jpeg")
    val imageJpeg: String? = null,
    @SerializedName("text/html")
    @SerialName("text/html")
    val textHtml: String? = null,
    /** Catalog metadata (RDF/XML), not the book; never open this for reading. */
    @SerializedName("application/rdf+xml")
    @SerialName("application/rdf+xml")
    val rdf: String? = null,
    /** EPUB download (a zip); a WebView can't display it. */
    @SerializedName("application/epub+zip")
    @SerialName("application/epub+zip")
    val epub: String? = null,
    @SerializedName("text/html; charset=utf-8")
    @SerialName("text/html; charset=utf-8")
    val textHtmlUtf8: String? = null,
    @SerializedName("text/plain; charset=utf-8")
    @SerialName("text/plain; charset=utf-8")
    val textPlainUtf8: String? = null,
    @SerializedName("text/plain; charset=us-ascii")
    @SerialName("text/plain; charset=us-ascii")
    val textPlainAscii: String? = null,
    @SerializedName("text/plain")
    @SerialName("text/plain")
    val textPlain: String? = null,
) {
    /** The best link to read the book in the app: HTML first, then plain text. */
    val readableUrl: String?
        get() = listOf(textHtml, textHtmlUtf8, textPlainUtf8, textPlainAscii, textPlain)
            .firstOrNull { !it.isNullOrBlank() }
}
