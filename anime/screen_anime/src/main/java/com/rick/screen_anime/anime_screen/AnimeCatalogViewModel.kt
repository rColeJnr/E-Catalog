package com.rick.screen_anime.anime_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data_anime.JikanRepository
import com.rick.data_anime.favorite.JikanFavorite
import com.rick.data_anime.model_jikan.Jikan
import com.rick.screen_anime.favorite_screen.JikanEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeCatalogViewModel @Inject constructor(
    private val repo: JikanRepository
) : ViewModel() {

    val pagingDataFlow: Flow<PagingData<Jikan>>

    init {
        pagingDataFlow = fetchAnimes().cachedIn(viewModelScope)
    }

    fun onEvent(event: JikanEvents) {
        when (event) {
            is JikanEvents.ShouldInsertFavorite -> insertFavorite(event.fav)
            else -> {}
        }
    }

    private fun insertFavorite(favorite: JikanFavorite) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertFavorite(favorite)
        }
    }

    private fun fetchAnimes(): Flow<PagingData<Jikan>> =
        repo.fetchAnime()
}
