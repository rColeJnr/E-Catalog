package com.rick.data.model_book

data class Favorite(
//    @SerializedName("title")
    val title: String,
//    @SerializedName("authors")
    val author: String,
//    @SerializedName("image")
    val image: String,
//    @SerializedName("favorite")
    val isFavorite: Boolean
)