package com.rick.anime.screen_anime.manga_catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.anime.anime_screen.common.JikanUiEvents
import com.rick.anime.data_anime.data.repository.manga.CompositeMangaRepository
import com.rick.anime.data_anime.data.repository.manga.UserMangaDataRepository
import com.rick.data.model_anime.UserManga
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