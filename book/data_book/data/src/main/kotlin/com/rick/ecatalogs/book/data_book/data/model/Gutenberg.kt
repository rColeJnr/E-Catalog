package com.rick.ecatalogs.book.data_book.data.model

import com.rick.ecatalogs.book.data_book.database.model.GutenbergEntity
import com.rick.ecatalogs.book.data_book.database.model.GutenbergRecentSearchQueryEntity
import com.rick.ecatalogs.book.data_book.model.gutenberg.GutenbergRecentSearchQuery
import com.rick.ecatalogs.book.data_book.network.model.GutenbergNetwork

fun GutenbergNetwork.asGutenbergEntity() = GutenbergEntity(
    id = id,
    title = title,
    authors = authors!!,
    bookshelves = bookshelves!!,
    downloads = downloads,
    formats = formats!!,
    mediaType = mediaType,
)

fun GutenbergRecentSearchQueryEntity.asExternalModel() = GutenbergRecentSearchQuery(
    query = query,
    queriedDate = queriedDate,
)