package com.rick.data.model_book.bestseller


data class Bestseller(
    val id: String,
    val rank: Int,
    val rankLastWeek: Int,
    val weeksOnList: Int,
    val publisher: String,
    val description: String,
    val title: String,
    val author: String,
    val image: String,
    val amazonLink: String,
    val buyLinks: List<BuyLink>
)