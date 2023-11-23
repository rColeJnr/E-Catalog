package com.rick.data_book.nytimes.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "ny_book")
data class NYBook(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("rank")
    val rank: Int,
    @SerializedName("rank_last_week")
    val rankLastWeek: Int,
    @SerializedName("weeks_on_list")
    val weeksOnList: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("book_image")
    val bookImage: String,
    @SerializedName("amazon_product_url")
    val amazonLink: String,
)