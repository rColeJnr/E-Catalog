package com.rick.ecatalogs.book.screen_book.gutenberg_catalog

import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.rick.ecatalogs.book.data_book.model.gutenberg.GutenbergFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.ecatalogs.book.data_book.data.repository.gutenberg.CompositeGutenbergRepository
import com.rick.ecatalogs.book.data_book.data.repository.gutenberg.UserGutenbergDataRepository
import com.rick.ecatalogs.book.data_book.model.gutenberg.UserGutenberg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GutenbergCatalogViewModel @Inject constructor(
    private val userDataRepository: UserGutenbergDataRepository,
    private val compositeBookRepository: CompositeGutenbergRepository
) : ViewModel() {

    /** Language and topic filters (Gutendex). The default shows the most popular books. */
    private val _filter = MutableStateFlow(GutenbergFilter())
    val filter: StateFlow<GutenbergFilter> = _filter.asStateFlow()

    val pagingDataFlow: Flow<PagingData<UserGutenberg>> = _filter
        .flatMapLatest { compositeBookRepository.observeGutenberg(viewModelScope, it) }
        .cachedIn(viewModelScope)

    fun setFilter(filter: GutenbergFilter) {
        _filter.value = filter
    }

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