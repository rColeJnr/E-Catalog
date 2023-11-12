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
    @SerializedName("publisher")
    val publisher: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("contributor")
    val contributor: String,
    @SerializedName("book_image")
    val bookImage: String,
    @SerializedName("book_review_link")
    val bookReviewLink: String,
    @SerializedName("buy_links")
    val buyLinks: List<BuyLink>,
    @SerializedName("book_uri")
    val bookUri: String
)