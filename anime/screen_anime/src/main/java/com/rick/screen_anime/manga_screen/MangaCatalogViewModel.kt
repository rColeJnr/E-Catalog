package com.rick.screen_anime.manga_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data.anime_favorite.repository.CompositeMangaRepository
import com.rick.data.anime_favorite.repository.UserMangaDataRepository
import com.rick.data.model_anime.UserManga
import com.rick.screen_anime.anime_screen.JikanUiEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaCatalogViewModel @Inject constructor(
    private val userDataRepository: UserMangaDataRepository,
    private val compositeMangaRepository: CompositeMangaRepository
) : ViewModel() {

    val pagingDataFlow: Flow<PagingData<UserManga>>

    init {
        pagingDataFlow = fetchManga().cachedIn(viewModelScope)
    }

    private fun fetchManga(): Flow<PagingData<UserManga>> =
        compositeMangaRepository.observeManga(viewModelScope)

    fun onEvent(event: JikanUiEvents) {
        when (event) {
            is JikanUiEvents.UpdateMangaFavorite -> updateMangaFavorite(
                id = event.id,
                event.isFavorite
            )

            else -> {}
        }
    }

    private fun updateMangaFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataRepository.setMangaFavoriteId(id, isFavorite)
        }
    }

}