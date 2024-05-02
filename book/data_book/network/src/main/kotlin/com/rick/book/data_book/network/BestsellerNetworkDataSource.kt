package com.rick.book.data_book.network

import com.rick.book.data_book.network.model.BestsellerResponse
import com.rick.book.data_book.network.model.SearchBestsellerResponse

interface BestsellerNetworkDataSource {
    suspend fun fetchBestsellers(
        date: String,
        bookGenre: String,
        apiKey: String,
    ): BestsellerResponse

    suspend fun searchBestseller(
        query: String,
        apiKey: String
    ): SearchBestsellerResponse

    companion object {
        const val BASE_URL = "https://api.nytimes.com/"
        private const val DATE = "current"
    }

}