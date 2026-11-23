package com.rick.ecatalogs.book.data_book.network.model

import kotlinx.serialization.Serializable

@Serializable
data class GutenbergResponse(
    val results: List<GutenbergNetwork>
)