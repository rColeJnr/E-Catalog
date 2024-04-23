package com.rick.screen_movie.search_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.data.model_movie.UserArticle
import com.rick.data.model_movie.UserTrendingSeries
import com.rick.data.movie_favorite.repository.article.CompositeArticleRepository
import com.rick.screen_movie.util.LIB_NAME
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

// WHY ISNT MY CODEBASE CONSISTENT THROUGHOUT, AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHHHHH
// I'm in public so i can't just blast.
// TODO
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: CompositeArticleRepository
) : ViewModel() {

    private val tmdbKey: String
    private var favorite: UserArticle? = null

    private val _searchList: MutableLiveData<SearchUiState.Response> by
    lazy { MutableLiveData<SearchUiState.Response>() }
    val searchList: LiveData<SearchUiState.Response> get() = _searchList

    private val _seriesList: MutableLiveData<SearchUiState.Series> by
    lazy { MutableLiveData<SearchUiState.Series>() }
    val seriesList: LiveData<SearchUiState.Series> get() = _seriesList

    private val _searchLoading: MutableLiveData<SearchUiState.Loading> by
    lazy { MutableLiveData<SearchUiState.Loading>(SearchUiState.Loading(false)) }
    val searchLoading: LiveData<SearchUiState.Loading> get() = _searchLoading

    private val _searchError: MutableLiveData<SearchUiState.Error> by
    lazy { MutableLiveData<SearchUiState.Error>() }
    val searchError: LiveData<SearchUiState.Error> get() = _searchError

    val searchState: StateFlow<SearchUiState.Query>
    val searchAction: (SearchUiEvent) -> Unit

    init {

        // Load api_keys
        System.loadLibrary(LIB_NAME)
//        imdbKey = getIMDBKey()
        tmdbKey = getTMDBKey()

        val actionStateFlow = MutableSharedFlow<SearchUiEvent>()
        val search =
            actionStateFlow.filterIsInstance<SearchUiEvent.SearchQuery>().distinctUntilChanged()

        viewModelScope.launch {
            search.collectLatest {
                searchTmdb(it.query)
            }
        }

        searchState = search.map { SearchUiState.Query(query = it.query) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 1000),
                initialValue = SearchUiState.Query(null)
            )

        searchAction = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun searchTmdb(title: String) {
        viewModelScope.launch {

        }
    }
//
//    private fun searchSeries(title: String) {
//        viewModelScope.launch {
//            repository.searchSeries(apiKey = imdbKey, query = title).collect { result ->
//                when (result) {
//                    is Resource.Error -> {
//                        _searchError.postValue(result.message)
//                    }
//
//                    is Resource.Loading -> {
//                        _searchLoading.postValue(result.isLoading)
//                    }
//
//                    is Resource.Success -> {
//                        _moviesList.postValue(result.data!!)
//                    }
//                }
//            }
//        }
//    }

    fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.InsertFavorite -> insertFavorite(event.favorite)
            is SearchUiEvent.RemoveFavorite -> removeFavorite()
            else -> {}
        }
    }

    private fun insertFavorite(favorite: com.rick.data.model_movie.tmdb.search.Search) {
//        this.favorite = favorite
        viewModelScope.launch(Dispatchers.IO) {
//            repository.insertFavorite(this@SearchViewModel.favorite!!)
        }
    }

    private fun removeFavorite() {
        favorite?.let {
            viewModelScope.launch(Dispatchers.IO) {
//                repository.removeFavorite(it)
            }
        }
        favorite = null
    }

}

private external fun getTMDBKey(): String

sealed class SearchUiState {
    data class Query(val query: String?)
    data class Response(val response: List<com.rick.data.model_movie.tmdb.search.Search>)
    data class Series(val series: List<UserTrendingSeries>?)
    data class Loading(val loading: Boolean)
    data class Error(val msg: String?)
}

sealed class SearchUiEvent {
    data class SearchQuery(val query: String) : SearchUiEvent()
    data class InsertFavorite(val favorite: com.rick.data.model_movie.tmdb.search.Search) :
        SearchUiEvent()

    object RemoveFavorite : SearchUiEvent()
}