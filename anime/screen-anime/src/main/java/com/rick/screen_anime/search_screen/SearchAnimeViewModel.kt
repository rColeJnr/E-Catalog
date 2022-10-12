package com.rick.screen_anime.search_screen

import androidx.lifecycle.ViewModel
import com.rick.data_anime.JikanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchAnimeViewModel @Inject constructor(
    private val repo: JikanRepository
) : ViewModel() {
}