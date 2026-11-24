package com.rick.ecatalogs.book.data_book.network

import com.rick.ecatalogs.book.data_book.network.model.GutenbergResponse
import retrofit2.http.Query

interface GutenbergNetworkDataSource {

    /** A page of books, most popular first, optionally by [languages] and [topic]. */
    suspend fun fetchBooks(
        page: Int,
        languages: String? = null,
        topic: String? = null,
    ): GutenbergResponse

    suspend fun searchGutenberg(
        @Query("search") query: String
    ): GutenbergResponse

}