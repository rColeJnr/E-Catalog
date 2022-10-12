package com.rick.screen_anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data_anime.JikanRepository
import com.rick.data_anime.model_anime.Anime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class AnimeCatalogViewModel @Inject constructor(
    private val repo: JikanRepository
) : ViewModel() {

    val pagingDataFlow: Flow<PagingData<Anime>>

    init {

        pagingDataFlow = fetchBooks().cachedIn(viewModelScope)
    }

    private fun fetchBooks(): Flow<PagingData<Anime>> =
        repo.fetchAnimes()
}
