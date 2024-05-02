package com.rick.anime.screen_anime.manga_favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.anime.data_anime.data.repository.manga.UserMangaDataRepository
import com.rick.anime.data_anime.data.repository.manga.UserMangaRepository
import com.rick.data.model_anime.FavoriteUiEvents
import com.rick.data.model_anime.FavoriteUiState
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
class MangaFavoriteViewModel @Inject constructor(
    private val userMangaDataRepository: UserMangaDataRepository,
    userMangaRepository: UserMangaRepository,
) : ViewModel() {

    val shouldDisplayMangaUndoFavorite = MutableStateFlow(false)

    private var lastRemovedFavorite: Int? = null

    val feedMangaUiState: StateFlow<FavoriteUiState> = userMangaRepository.observeMangaFavorite()
        .map<List<UserManga>, FavoriteUiState>(FavoriteUiState::MangaFavorites)
        .onStart { emit(FavoriteUiState.Loading) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = FavoriteUiState.Loading
        )

    fun onEvent(event: FavoriteUiEvents) {
        when (event) {
            is FavoriteUiEvents.RemoveMangaFavorite -> removeMangaFavorite(event.mangaId)
            is FavoriteUiEvents.UndoMangaFavoriteRemoval -> undoMangaFavoriteRemoval()
            is FavoriteUiEvents.ClearUndoState -> clearUndoState()
            else -> {}
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

    private fun removeMangaFavorite(favoriteId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            shouldDisplayMangaUndoFavorite.value = true
            lastRemovedFavorite = favoriteId
            userMangaDataRepository.setMangaFavoriteId(favoriteId, false)
        }
    }

    private fun clearUndoState() {
        shouldDisplayMangaUndoFavorite.value = false
        lastRemovedFavorite = null
    }
}

private const val SHOW_ANIME = "showAnime"
private const val SHOW_MANGA = "showManga"
