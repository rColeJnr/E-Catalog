package com.rick.data_book.nytimes.model


import com.google.gson.annotations.SerializedName

data class Book(
    @SerializedName("rank")
    val rank: Int,
    @SerializedName("rank_last_week")
    val rankLastWeek: Int,
    @SerializedName("weeks_on_list")
    val weeksOnList: Int,
    @SerializedName("primary_isbn10")
    val primaryIsbn10: String,
    @SerializedName("primary_isbn13")
    val primaryIsbn13: String,
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