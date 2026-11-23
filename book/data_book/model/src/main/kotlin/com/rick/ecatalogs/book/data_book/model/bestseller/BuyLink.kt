package com.rick.ecatalogs.book.data_book.model.bestseller

import kotlinx.serialization.Serializable

@Serializable
data class BuyLink(
    val name: String,
    val url: String
)