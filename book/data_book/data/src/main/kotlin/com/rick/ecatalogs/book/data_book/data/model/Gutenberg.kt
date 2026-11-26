package com.rick.ecatalogs.book.data_book.data.model

import com.rick.ecatalogs.book.data_book.database.model.GutenbergEntity
import com.rick.ecatalogs.book.data_book.model.gutenberg.Formats
import com.rick.ecatalogs.book.data_book.database.model.GutenbergRecentSearchQueryEntity
import com.rick.ecatalogs.book.data_book.model.gutenberg.GutenbergRecentSearchQuery
import com.rick.ecatalogs.book.data_book.network.model.GutenbergNetwork

fun GutenbergNetwork.asGutenbergEntity() = GutenbergEntity(
    id = id,
    title = title,
    // Gutendex may send null for these (Gson then ignores the Kotlin defaults)
    authors = authors.orEmpty(),
    bookshelves = bookshelves.orEmpty(),
    downloads = downloads,
    formats = formats ?: Formats(),
    mediaType = mediaType,
)

fun GutenbergRecentSearchQueryEntity.asExternalModel() = GutenbergRecentSearchQuery(
    query = query,
    queriedDate = queriedDate,
)