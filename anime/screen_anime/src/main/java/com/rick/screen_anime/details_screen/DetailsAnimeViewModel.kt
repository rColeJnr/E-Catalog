package com.rick.screen_anime.details_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rick.data.model_anime.UserAnime
import com.rick.data.model_anime.UserManga
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsAnimeViewModel @Inject constructor(
) : ViewModel() {

    // WHY do i need a viewModel? besides survive process death?

    private val _anime = MutableLiveData<UserAnime>()
    val anime: LiveData<UserAnime> get() = _anime

    fun setAnime(anime: UserAnime) {
        this._anime.value = anime
    }

    private val _manga = MutableLiveData<UserManga>()
    val manga: LiveData<UserManga> get() = _manga

    fun setAnime(manga: UserManga) {
        this._manga.value = manga
    }

}