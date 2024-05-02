package com.rick.book.data_book.network

import com.rick.book.data_book.network.model.GutenbergResponse
import retrofit2.http.Query

interface GutenbergNetworkDataSource {

    suspend fun fetchBooks(
        @Query("page") page: Int
    ): GutenbergResponse

    suspend fun searchGutenberg(
        @Query("search") query: String
    ): GutenbergResponse

}