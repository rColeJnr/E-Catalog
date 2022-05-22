package com.rick.data_anime

data class Data(
    val count: Int,
    val current_page: Int,
    val anime: List<Anime>,
    val last_page: Int
)