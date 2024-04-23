package com.rick.data.model_movie.tmdb.movie

import com.rick.data.model_movie.tmdb.Similar

data class MovieSimilar(
    val id: Int,
    val posterPath: String,
    val title: String,
) {
    fun toSimilar(): Similar =
        Similar(
            id, posterPath, title
        )
}

