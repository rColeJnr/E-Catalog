package com.rick.ecatalogs.anime.data_anime.model.model_jikan

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