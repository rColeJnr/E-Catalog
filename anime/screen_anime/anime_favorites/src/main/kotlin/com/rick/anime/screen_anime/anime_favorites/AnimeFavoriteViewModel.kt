package com.rick.anime.screen_anime.anime_favorites

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.anime.data_anime.data.repository.anime.UserAnimeDataRepository
import com.rick.anime.data_anime.data.repository.anime.UserAnimeRepository
import com.rick.anime.data_anime.data.repository.manga.UserMangaDataRepository
import com.rick.anime.data_anime.data.repository.manga.UserMangaRepository
import com.rick.data.model_anime.FavoriteUiEvents
import com.rick.data.model_anime.FavoriteUiState
import com.rick.data.model_anime.UserAnime
import com.rick.data.model_anime.UserManga
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeFavoriteViewModel @Inject constructor(
    private val userAnimeDataRepository: UserAnimeDataRepository,
    private val userMangaDataRepository: UserMangaDataRepository,
    userAnimeRepository: UserAnimeRepository,
    userMangaRepository: UserMangaRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val showAnime = savedStateHandle.getStateFlow(key = SHOW_ANIME, initialValue = false)
    val showManga = savedStateHandle.getStateFlow(key = SHOW_MANGA, initialValue = false)
    val shouldDisplayAnimeUndoFavorite = MutableStateFlow(false)
    val shouldDisplayMangaUndoFavorite = MutableStateFlow(false)

    private var lastRemovedFavorite: Int? = null

    val feedAnimeUiState: StateFlow<FavoriteUiState> = userAnimeRepository.observeAnimeFavorite()
        .map<List<UserAnime>, FavoriteUiState>(FavoriteUiState::AnimeFavorites)
        .onStart { emit(FavoriteUiState.Loading) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = FavoriteUiState.Loading
        )

    val feedMangaUiState: StateFlow<FavoriteUiState> = userMangaRepository.observeMangaFavorite()
        .map<List<UserManga>, FavoriteUiState>(FavoriteUiState::MangaFavorites)
        .onStart { emit(FavoriteUiState.Loading) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = FavoriteUiState.Loading
        )

    fun onEvent(event: FavoriteUiEvents) {
        when (event) {
            is FavoriteUiEvents.RemoveAnimeFavorite -> removeAnimeFavorite(event.animeId)
            is FavoriteUiEvents.RemoveMangaFavorite -> removeMangaFavorite(event.mangaId)
            is FavoriteUiEvents.UndoMangaFavoriteRemoval -> undoMangaFavoriteRemoval()
            is FavoriteUiEvents.UndoAnimeFavoriteRemoval -> undoAnimeFavoriteRemoval()
            is FavoriteUiEvents.ClearUndoState -> clearUndoState()
            is FavoriteUiEvents.ShouldShowAnime -> shouldShowAnime(event.show)
            is FavoriteUiEvents.ShouldShowManga -> shouldShowManga(event.show)
        }
    }

    private fun undoAnimeFavoriteRemoval() {
        viewModelScope.launch(Dispatchers.IO) {
            lastRemovedFavorite?.let {
                userAnimeDataRepository.setAnimeFavoriteId(it, true)
            }
        }
    }

    private fun undoMangaFavoriteRemoval() {
        viewModelScope.launch(Dispatchers.IO) {
            lastRemovedFavorite?.let {
                userMangaDataRepository.setMangaFavoriteId(it, true)
            }
        }
        clearUndoState()
    }

    private fun removeAnimeFavorite(favoriteId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            shouldDisplayAnimeUndoFavorite.value = true
            lastRemovedFavorite = favoriteId
            userAnimeDataRepository.setAnimeFavoriteId(favoriteId, false)
        }
    }

    private fun removeMangaFavorite(favoriteId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            shouldDisplayMangaUndoFavorite.value = true
            lastRemovedFavorite = favoriteId
            userMangaDataRepository.setMangaFavoriteId(favoriteId, false)
        }
    }

    private fun clearUndoState() {
        shouldDisplayAnimeUndoFavorite.value = false
        shouldDisplayMangaUndoFavorite.value = false
        lastRemovedFavorite = null
    }

    private fun shouldShowAnime(show: Boolean) {
        savedStateHandle[SHOW_ANIME] = show
    }

    private fun shouldShowManga(show: Boolean) {
        savedStateHandle[SHOW_MANGA] = show
    }
}

private const val SHOW_ANIME = "showAnime"
private const val SHOW_MANGA = "showManga"
