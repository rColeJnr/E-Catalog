package com.rick.data.model_book.gutenberg

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Parcelize
@Serializable
data class Formats(
    @SerializedName("image/jpeg")
    @SerialName("image/jpeg")
    val imageJpeg: String?,
    @SerializedName("text/html")
    @SerialName("text/html")
    val textHtml: String?,
    @SerializedName("application/rdf+xml")
    @SerialName("application/rdf+xml")
    val textPlain: String?,
    @SerializedName("application/epub+zip")
    @SerialName("application/epub+zip")
    val textPlainCharsetUtf8: String,
) /*: Parcelable*/