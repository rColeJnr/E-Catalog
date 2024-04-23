package com.rick.data.model_anime.model_jikan

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pagination(
    @SerializedName("last_visible_page")
    @SerialName("last_visible_page")
    val lastVisiblePage: Int,
    @SerializedName("has_next_page")
    @SerialName("has_next_page")
    val hasNextPage: Boolean,
    @SerializedName("current_page")
    @SerialName("current_page")
    val currentPage: Int
)