package com.rick.screen_anime.manga_screen

import androidx.lifecycle.ViewModel
import com.rick.data_anime.JikanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MangaCatalogViewModel @Inject constructor(
    private val repo: JikanRepository
) : ViewModel() {
}