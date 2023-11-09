package com.rick.data_book.nytimes.model


import com.google.gson.annotations.SerializedName

data class BuyLink(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
)