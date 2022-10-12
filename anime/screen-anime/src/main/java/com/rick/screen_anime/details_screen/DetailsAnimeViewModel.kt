package com.rick.screen_anime.details_screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsAnimeViewModel @Inject constructor(
    private val repo: JikanRepository
): ViewModel() {
}