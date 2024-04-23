package com.rick.data.network_book

import com.rick.data.network_book.model.GutenbergResponse
import retrofit2.http.Query

interface GutenbergNetworkDataSource {

    suspend fun fetchBooks(
        @Query("page") page: Int
    ): GutenbergResponse

    suspend fun searchBook(
        @Query("search") query: String
    ): GutenbergResponse

}