package com.rick.data_anime

data class AnimeResponse(
    val `data`: Data,
    val message: String,
    val status_code: Int,
    val version: String
){
    companion object {
        const val link = "https://api.aniapi.com/v1/anime?title=One%20Piece"
    }
}

