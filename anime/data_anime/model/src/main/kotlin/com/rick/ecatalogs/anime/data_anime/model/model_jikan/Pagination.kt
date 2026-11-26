package com.rick.ecatalogs.anime.data_anime.model.model_jikan

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Retrofit parses Jikan with Gson, which ignores @SerialName: without @SerializedName,
// has_next_page was never read and every Jikan list stopped after its first page.
@Serializable
data class Pagination(
    @SerialName("current_page")
    @SerializedName("current_page")
    val currentPage: Int,
    @SerialName("has_next_page")
    @SerializedName("has_next_page")
    val hasNextPage: Boolean,
    @SerialName("items")
    @SerializedName("items")
    val items: Items,
    @SerialName("last_visible_page")
    @SerializedName("last_visible_page")
    val lastVisiblePage: Int
) {
    @Serializable
    data class Items(
        @SerialName("count")
        val count: Int,
        @SerialName("per_page")
        @SerializedName("per_page")
        val perPage: Int,
        @SerialName("total")
        val total: Int
    )
}
