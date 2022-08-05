package com.rick.screen_movie

import com.rick.data_movie.MovieEntity

sealed class UiModel {
    data class MovieItem(val movie: MovieEntity): UiModel()
    data class SeparatorItem(val description: String): UiModel()
}

