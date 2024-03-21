package com.rick.data_movie.tmdb.search

import com.google.gson.annotations.SerializedName
import com.rick.data_movie.favorite.Favorite

data class Search(
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("name")
    val title: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("overview")
    val summary: String,
    @SerializedName("poster_path")
    val image: String,
    @SerializedName("media_type")
    val mediaType: String,
) {
    fun toFavorite(): Favorite =
        Favorite(
            id = id,
            title = title,
            summary = summary,
            image = image,
            type = "tv",
            isFavorite = false
        )
}