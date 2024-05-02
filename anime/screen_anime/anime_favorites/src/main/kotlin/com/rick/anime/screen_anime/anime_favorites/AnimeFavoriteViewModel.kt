package com.rick.anime.screen_anime.anime_favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.anime.data_anime.data.repository.anime.UserAnimeDataRepository
import com.rick.anime.data_anime.data.repository.anime.UserAnimeRepository
import com.rick.data.model_anime.FavoriteUiEvents
import com.rick.data.model_anime.FavoriteUiState
import com.rick.data.model_anime.UserAnime
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
    userAnimeRepository: UserAnimeRepository,
) : ViewModel() {

    val shouldDisplayAnimeUndoFavorite = MutableStateFlow(false)

    private var lastRemovedFavorite: Int? = null

    val feedAnimeUiState: StateFlow<FavoriteUiState> = userAnimeRepository.observeAnimeFavorite()
        .map<List<UserAnime>, FavoriteUiState>(FavoriteUiState::AnimeFavorites)
        .onStart { emit(FavoriteUiState.Loading) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = FavoriteUiState.Loading
        )

    fun onEvent(event: FavoriteUiEvents) {
        when (event) {
            is FavoriteUiEvents.RemoveAnimeFavorite -> removeAnimeFavorite(event.animeId)
            is FavoriteUiEvents.UndoAnimeFavoriteRemoval -> undoAnimeFavoriteRemoval()
            is FavoriteUiEvents.ClearUndoState -> clearUndoState()
            else -> {}
        }
    }

    private fun undoAnimeFavoriteRemoval() {
        viewModelScope.launch(Dispatchers.IO) {
            lastRemovedFavorite?.let {
                userAnimeDataRepository.setAnimeFavoriteId(it, true)
            }
        }
    }

    private fun removeAnimeFavorite(favoriteId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            shouldDisplayAnimeUndoFavorite.value = true
            lastRemovedFavorite = favoriteId
            userAnimeDataRepository.setAnimeFavoriteId(favoriteId, false)
        }
    }

    private fun clearUndoState() {
        shouldDisplayAnimeUndoFavorite.value = false
        lastRemovedFavorite = null
    }
}

private const val SHOW_ANIME = "showAnime"
private const val SHOW_MANGA = "showManga"
