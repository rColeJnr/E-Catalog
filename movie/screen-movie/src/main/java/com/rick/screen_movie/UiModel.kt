package com.rick.screen_movie

import com.rick.data_movie.Result

sealed class UiModel {
    data class MovieItem(val movie: Result): UiModel()
    data class SeparatorItem(val description: String): UiModel()
}

