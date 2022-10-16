package com.rick.screen_anime.details_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rick.data_anime.model_jikan.Jikan
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsAnimeViewModel @Inject constructor(
) : ViewModel() {

    // WHY do i need a viewModel? besides survive process death?

    private val _jikan = MutableLiveData<Jikan>()
    val jikan: LiveData<Jikan> get() = _jikan

    fun setJikan(anime: Jikan) {
        this._jikan.value = anime
    }

}