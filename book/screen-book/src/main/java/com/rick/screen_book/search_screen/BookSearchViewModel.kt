package com.rick.screen_book.search_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_book.BookRepository
import com.rick.data_book.favorite.Favorite
import com.rick.data_book.model.Book
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
    private val repository: BookRepository
) : ViewModel() {

    private val _searchList: MutableLiveData<List<Book>> by
    lazy { MutableLiveData<List<Book>>() }
    val searchList: LiveData<List<Book>> get() = _searchList

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
            is SearchUiAction.InsertFavorite -> insertFavorite(event.favorite)
            else -> {}
        }
    }

    private fun insertFavorite(favorite: Favorite) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(favorite)
        }
    }

    private fun searchBooks(query: String) {
        viewModelScope.launch {
            repository.searchBooks(query).collect{ result ->
                when (result) {
                    is Resource.Error -> {
                        _searchError.postValue(result.message)
                    }
                    is Resource.Loading -> {
                        _searchLoading.postValue(result.isLoading)
                    }
                    is Resource.Success -> {
                        _searchList.postValue(result.data!!)
                    }
                }
            }
        }
    }

}

sealed class SearchUiAction {
    data class SearchBooks(val query: String) : SearchUiAction()
    data class InsertFavorite(val favorite: Favorite): SearchUiAction()
}

data class SearchUiState(
    val searchQuery: String? = null
)