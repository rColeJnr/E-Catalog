package com.rick.screen_anime.manga_screen

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
class MangaCatalogViewModel @Inject constructor(
    private val repo: JikanRepository
) : ViewModel() {

    val pagingDataFlow: Flow<PagingData<Jikan>>

    init {

        pagingDataFlow = fetchManga().cachedIn(viewModelScope)
    }

    private fun fetchManga(): Flow<PagingData<Jikan>> =
        repo.fetchManga()

    fun onEvent(event: JikanEvents) {
        when (event) {
            is JikanEvents.ShouldInsertFavorite -> insertFavorite(favorite = event.fav)
            else -> {}
        }
    }

    private fun insertFavorite(favorite: JikanFavorite) {
        viewModelScope.launch(Dispatchers.IO) { repo.insertFavorite(favorite = favorite) }
    }

}