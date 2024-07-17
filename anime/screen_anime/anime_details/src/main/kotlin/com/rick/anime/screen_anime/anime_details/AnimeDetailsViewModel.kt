package com.rick.anime.screen_anime.anime_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rick.data.domain_anime.GetAnimeByIdUseCase
import com.rick.data.model_anime.UserAnime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class AnimeDetailsViewModel @Inject constructor(
    private val getAnimeByIdUseCase: GetAnimeByIdUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // WHY do i need a viewModel? besides survive process death?
    private val animeId = savedStateHandle.getStateFlow(key = ANIME_ID, initialValue = -1)

    fun setAnimeId(id: Int) {
        savedStateHandle[ANIME_ID] = id
    }

    fun getAnime(): Flow<UserAnime> =
        getAnimeByIdUseCase(animeId.value)
}

private const val ANIME_ID = "anime"