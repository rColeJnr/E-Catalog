package com.rick.book.data_book.network.model

import com.google.gson.annotations.SerializedName
import com.rick.data.model_book.bestseller.BestsellerSearchResult
import kotlinx.serialization.SerialName

data class SearchBestsellerResponse(
    @SerializedName("num_results")
    @SerialName("num_results")
    val numResults: Int,
    val results: List<BestsellerSearchResult>
)