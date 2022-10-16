package com.rick.screen_anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data_anime.JikanRepository
import com.rick.data_anime.model_jikan.Jikan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class AnimeCatalogViewModel @Inject constructor(
    private val repo: JikanRepository
) : ViewModel() {

    val pagingDataFlow: Flow<PagingData<Jikan>>

    init {

        pagingDataFlow = fetchAnimes().cachedIn(viewModelScope)
    }

    private fun fetchAnimes(): Flow<PagingData<Jikan>> =
        repo.fetchAnimes()
}
