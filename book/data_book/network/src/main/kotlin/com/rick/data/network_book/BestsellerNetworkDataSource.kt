package com.rick.data.network_book

import com.rick.data.network_book.model.BestsellerResponse
import com.rick.data.network_book.model.SearchBestsellerResponse

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