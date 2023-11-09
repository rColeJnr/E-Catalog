package com.rick.data_book.nytimes.model


import com.google.gson.annotations.SerializedName

data class Results(
    @SerializedName("list_name")
    val listName: String,
    @SerializedName("bestsellers_date")
    val bestsellersDate: String,
    @SerializedName("normal_list_ends_at")
    val normalListEndsAt: Int,
    @SerializedName("updated")
    val updated: String,
    @SerializedName("books")
    val books: List<Book>
)