package com.rick.data.model_anime.model_jikan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pagination(
    @SerialName("current_page")
    val currentPage: Int,
    @SerialName("has_next_page")
    val hasNextPage: Boolean,
    @SerialName("items")
    val items: Items,
    @SerialName("last_visible_page")
    val lastVisiblePage: Int
) {
    @Serializable
    data class Items(
        @SerialName("count")
        val count: Int,
        @SerialName("per_page")
        val perPage: Int,
        @SerialName("total")
        val total: Int
    )
}