package com.rick.book.data_book.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class BestsellerResponse(
    @SerializedName("num_results")
    val num_results: Int,
    val results: Results
)