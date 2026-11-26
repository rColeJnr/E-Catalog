package com.rick.ecatalogs.book.data_book.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rick.ecatalogs.book.data_book.model.gutenberg.Author
import com.rick.ecatalogs.book.data_book.model.gutenberg.Formats
import com.rick.ecatalogs.book.data_book.model.gutenberg.Gutenberg

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
    /** Place in the catalog list (the API's order); null for rows kept only as search results or favorites. */
    @ColumnInfo("catalog_position")
    val catalogPosition: Int? = null,
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
