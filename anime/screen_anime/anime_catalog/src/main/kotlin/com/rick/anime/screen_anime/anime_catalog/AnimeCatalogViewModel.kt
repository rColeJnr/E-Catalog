package com.rick.anime.screen_anime.anime_catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.anime.anime_screen.common.JikanUiEvents
import com.rick.anime.data_anime.data.repository.anime.CompositeAnimeRepository
import com.rick.anime.data_anime.data.repository.anime.UserAnimeDataRepository
import com.rick.data.model_anime.UserAnime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeCatalogViewModel @Inject constructor(
    private val userDataRepository: UserAnimeDataRepository,
    private val compositeAnimeRepository: CompositeAnimeRepository,
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

private const val LOCATION_QUERY = "LocationQuery"
private const val FOLDER_ID = "b1g3qbtiprdplbrpod5i"