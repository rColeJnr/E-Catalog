package com.rick.screen_book.search_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.data_book.repository.gutenberg.CompositeGutenbergRepository
import com.rick.data.data_book.repository.gutenberg.UserGutenbergDataRepository
import com.rick.data.model_book.UserGutenberg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookSearchViewModel @Inject constructor(
    private val compositeBookRepository: CompositeGutenbergRepository,
    private val userDataRepository: UserGutenbergDataRepository
) : ViewModel() {

    private val _searchList: MutableLiveData<List<UserGutenberg>> by
    lazy { MutableLiveData<List<UserGutenberg>>() }
    val searchList: LiveData<List<UserGutenberg>> get() = _searchList

    private val _searchLoading: MutableLiveData<Boolean> by
    lazy { MutableLiveData<Boolean>(false) }
    val searchLoading: LiveData<Boolean> get() = _searchLoading

    private val _searchError: MutableLiveData<String> by
    lazy { MutableLiveData<String>() }
    val searchError: LiveData<String> get() = _searchError

    val searchUiAction: (SearchUiAction) -> Unit
    val searchUiState: StateFlow<SearchUiState>

    init {

        val actionStateFlow = MutableSharedFlow<SearchUiAction>()
        val searchBooks =
            actionStateFlow.filterIsInstance<SearchUiAction.SearchBooks>().distinctUntilChanged()

        viewModelScope.launch {
            searchBooks.collectLatest {
                searchBooks(it.query)
            }
        }

        searchUiState = searchBooks.map { SearchUiState(searchQuery = it.query) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000L),
                initialValue = SearchUiState()
            )

        searchUiAction = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    fun onEvent(event: SearchUiAction) {
        when (event) {
            is SearchUiAction.UpdateGutenbergFavorite -> updateGutenbergFavorite(
                event.id,
                event.isFavorite
            )

            else -> {}
        }
    }

    private fun updateGutenbergFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataRepository.setGutenbergFavoriteId(id, isFavorite)
        }
    }

    private fun searchBooks(query: String) {
        viewModelScope.launch {
            //TODO()
        }
    }

}

sealed class SearchUiAction {
    data class SearchBooks(val query: String) : SearchUiAction()
    data class UpdateGutenbergFavorite(val id: Int, val isFavorite: Boolean) : SearchUiAction()
}

data class SearchUiState(
    val searchQuery: String? = null
)