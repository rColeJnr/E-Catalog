package com.rick.data.network_book.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
data class Results(
    @SerializedName("list_name")
    val list_name: String,
    val bestsellers_date: String,
    @SerializedName("previous_published_date")
    val previous_published_date: String,
    val books: List<BestsellerNetwork>
)