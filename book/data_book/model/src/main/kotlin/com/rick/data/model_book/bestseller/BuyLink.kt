package com.rick.data.model_book.bestseller

import kotlinx.serialization.Serializable

@Serializable
data class BuyLink(
    val name: String,
    val url: String
)