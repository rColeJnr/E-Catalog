package com.rick.data.network_book.model

import kotlinx.serialization.Serializable

@Serializable
data class GutenbergResponse(
    val results: List<GutenbergNetwork>
)