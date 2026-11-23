package com.rick.ecatalogs.book.data_book.network.model

import com.google.gson.annotations.SerializedName
import com.rick.ecatalogs.book.data_book.model.gutenberg.Author
import com.rick.ecatalogs.book.data_book.model.gutenberg.Formats
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GutenbergNetwork(
    val id: Int,
    val title: String,
    val authors: List<Author>? = listOf(),
    val bookshelves: List<String>? = listOf(),
    @SerializedName("media_type")
    @SerialName("media_type")
    val mediaType: String,
    val formats: Formats? = Formats(null, null, null, null),
    @SerializedName("download_count")
    @SerialName("download_count")
    val downloads: Int,
)
