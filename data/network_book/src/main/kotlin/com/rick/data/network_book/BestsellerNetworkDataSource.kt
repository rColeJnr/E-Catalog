package com.rick.data.network_book

import com.rick.data.network_book.model.BestsellerResponse

interface BestsellerNetworkDataSource {
    suspend fun fetchBestsellers(
        date: String,
        bookGenre: String,
        apiKey: String,
    ): BestsellerResponse

    companion object {
        const val BASE_URL = "https://api.nytimes.com/"
        private const val DATE = "current"
    }

}