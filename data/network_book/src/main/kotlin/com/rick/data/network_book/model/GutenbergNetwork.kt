package com.rick.data.network_book.model

import com.google.gson.annotations.SerializedName
import com.rick.data.model_book.gutenberg.Author
import com.rick.data.model_book.gutenberg.Formats
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GutenbergNetwork(
    val id: Int,
    val title: String,
    val authors: List<Author>,
    val bookshelves: List<String>,
    @SerializedName("media_type")
    @SerialName("media_type")
    val mediaType: String,
    val formats: Formats,
    @SerializedName("download_count")
    @SerialName("download_count")
    val downloads: Int,
)
