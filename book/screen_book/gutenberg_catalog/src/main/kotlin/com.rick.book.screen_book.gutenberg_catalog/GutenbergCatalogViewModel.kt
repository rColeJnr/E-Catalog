package com.rick.book.screen_book.gutenberg_catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.data.data_book.repository.gutenberg.CompositeGutenbergRepository
import com.rick.data.data_book.repository.gutenberg.UserGutenbergDataRepository
import com.rick.data.model_book.gutenberg.UserGutenberg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GutenbergCatalogViewModel @Inject constructor(
    private val userDataRepository: UserGutenbergDataRepository,
    private val compositeBookRepository: CompositeGutenbergRepository
) : ViewModel() {

    var pagingDataFlow: Flow<PagingData<UserGutenberg>>

    init {
        pagingDataFlow = fetchGutenbergs().cachedIn(viewModelScope)
    }

    private fun fetchGutenbergs(): Flow<PagingData<UserGutenberg>> =
        compositeBookRepository.observeGutenberg(viewModelScope)

    fun onEvent(event: BookUiEvents) {
        when (event) {
            is BookUiEvents.UpdateGutenbergFavorite -> updateGutenbergFavorite(
                event.id,
                event.isFavorite
            )
        }
    }

    private fun updateGutenbergFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataRepository.setGutenbergFavoriteId(id, isFavorite)
        }
    }
}

sealed interface BookUiEvents {
    data class UpdateGutenbergFavorite(val id: Int, val isFavorite: Boolean) : BookUiEvents
}