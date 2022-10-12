package com.rick.screen_anime.details_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rick.data_anime.model_anime.Anime
import com.rick.data_anime.model_manga.Manga
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsAnimeViewModel @Inject constructor(
) : ViewModel() {

    // WHY do i need a viewModel? besides survive process death?

    private val _anime = MutableLiveData<Anime>()
    val anime: LiveData<Anime> get() = _anime
    private val _manga = MutableLiveData<Manga>()
    val manga: LiveData<Manga> get() = _manga


    fun setAnimaValue(anime: Anime) {
        this._anime.value = anime
    }

    fun setMangaValue(manga: Manga) {
        this._manga.value = manga
    }

}