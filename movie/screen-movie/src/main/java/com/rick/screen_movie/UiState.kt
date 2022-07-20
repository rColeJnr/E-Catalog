package com.rick.screen_movie

import com.rick.data_movie.Result

data class UiState(
    val movies: Result,
    val isRefreshing: Boolean = false
)
