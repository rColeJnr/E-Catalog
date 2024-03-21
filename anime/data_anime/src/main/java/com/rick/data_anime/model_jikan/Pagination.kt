package com.rick.data_anime.model_jikan


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Pagination(
    @SerializedName("last_visible_page")
    val lastVisiblePage: Int,
    @SerializedName("has_next_page")
    val hasNextPage: Boolean,
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("items")
    val items: Items
) : Parcelable