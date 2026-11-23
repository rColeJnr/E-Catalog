package com.rick.ecatalogs.book.data_book.network

import com.rick.ecatalogs.book.data_book.network.model.BestsellerResponse
import com.rick.ecatalogs.book.data_book.network.model.SearchBestsellerResponse

interface BestsellerNetworkDataSource {
    suspend fun fetchBestsellers(
        date: String,
        bookGenre: String,
    ): BestsellerResponse

    suspend fun searchBestseller(
        query: String,
    ): SearchBestsellerResponse

}
