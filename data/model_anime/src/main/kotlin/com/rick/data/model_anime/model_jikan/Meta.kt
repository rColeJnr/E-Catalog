package com.rick.data.model_anime.model_jikan

data class Meta(
    val currentPage: Int,
    val from: Int,
    val lastPage: Int,
    val links: List<Link>,
    val path: String,
    val perPage: Int,
    val to: Int,
    val total: Int
)