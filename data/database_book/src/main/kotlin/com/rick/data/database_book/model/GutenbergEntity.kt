package com.rick.data.database_book.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rick.data.model_book.gutenberg.Author
import com.rick.data.model_book.gutenberg.Formats
import com.rick.data.model_book.gutenberg.Gutenberg

@Entity(tableName = "gutenberg_table")
data class GutenbergEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val authors: List<Author>,
    val bookshelves: List<String>,
    val downloads: Int,
    val formats: Formats,
    val mediaType: String,
)

fun GutenbergEntity.asGutenberg() = Gutenberg(
    id = id,
    title = title,
    authors = authors,
    bookshelves = bookshelves,
    downloads = downloads,
    formats = formats,
    mediaType = mediaType,
)
