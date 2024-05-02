package com.rick.data.data_book.model

import com.rick.data.database_book.model.GutenbergEntity
import com.rick.data.database_book.model.GutenbergRecentSearchQueryEntity
import com.rick.data.model_book.gutenberg.GutenbergRecentSearchQuery
import com.rick.book.data_book.network.model.GutenbergNetwork

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