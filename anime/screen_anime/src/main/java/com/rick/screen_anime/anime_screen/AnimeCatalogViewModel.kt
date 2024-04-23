package com.rick.screen_anime.anime_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data.anime_favorite.repository.CompositeAnimeRepository
import com.rick.data.anime_favorite.repository.UserAnimeDataRepository
import com.rick.data.model_anime.UserAnime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeCatalogViewModel @Inject constructor(
    private val userDataRepository: UserAnimeDataRepository,
    private val compositeAnimeRepository: CompositeAnimeRepository
) : ViewModel() {

    val pagingDataFlow: Flow<PagingData<UserAnime>>

    init {
        pagingDataFlow = fetchAnime().cachedIn(viewModelScope)
    }

    fun onEvent(event: JikanUiEvents) {
        when (event) {
            is JikanUiEvents.UpdateAnimeFavorite -> {
                updateAnimeFavorite(id = event.id, isFavorite = event.isFavorite)
            }

            is JikanUiEvents.UpdateMangaFavorite -> {
                // Do nothing
            }
        }
    }

    private fun updateAnimeFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataRepository.setAnimeFavoriteId(id, isFavorite)
        }
    }

    private fun fetchAnime(): Flow<PagingData<UserAnime>> =
        compositeAnimeRepository.observeAnime(viewModelScope)
}

sealed interface JikanUiEvents {
    data class UpdateAnimeFavorite(val id: Int, val isFavorite: Boolean) : JikanUiEvents
    data class UpdateMangaFavorite(val id: Int, val isFavorite: Boolean) : JikanUiEvents
}