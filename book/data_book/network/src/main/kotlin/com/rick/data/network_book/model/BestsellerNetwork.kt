package com.rick.data.network_book.model

import com.google.gson.annotations.SerializedName
import com.rick.data.model_book.bestseller.BuyLink
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BestsellerNetwork(
    @SerializedName("primary_isbn10")
    @SerialName("primary_isbn10")
    val id: String,
    val rank: Int,
    @SerializedName("rank_last_week")
    @SerialName("rank_last_week")
    val rankLastWeek: Int,
    @SerializedName("weeks_on_list")
    @SerialName("weeks_on_list")
    val weeksOnList: Int,
    val publisher: String,
    val description: String,
    val title: String,
    val author: String,
    @SerializedName("book_image")
    @SerialName("book_image")
    val image: String = "",
    @SerializedName("amazon_product_url")
    @SerialName("amazon_product_url")
    val amazonUrl: String,
    @SerializedName("buy_links")
    @SerialName("buy_links")
    val buyLinks: List<BuyLink>
)
