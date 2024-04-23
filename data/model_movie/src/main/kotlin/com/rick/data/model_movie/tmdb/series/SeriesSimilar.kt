package com.rick.data.model_movie.tmdb.series

import com.rick.data.model_movie.tmdb.Similar

data class SeriesSimilar(
    val id: Int,
    val posterPath: String,
    val name: String,
) {
    fun toSimilar(): Similar =
        Similar(
            id, posterPath, name
        )
}
