package com.rick.screen_movie

import com.rick.data_movie.ny_times.article_models.Doc

sealed class UiModel {
    data class MovieItem(val movie: Doc): UiModel()
    data class SeparatorItem(val description: String): UiModel()
}

