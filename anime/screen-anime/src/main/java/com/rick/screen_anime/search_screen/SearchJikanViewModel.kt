package com.rick.screen_anime.search_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_anime.JikanRepository
import com.rick.data_anime.model_anime.Anime
import com.rick.data_anime.model_manga.Manga
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchAnimeViewModel @Inject constructor(
    private val repo: JikanRepository
) : ViewModel() {

    private val _searchMangaList: MutableLiveData<List<Manga>> by
    lazy { MutableLiveData<List<Manga>>() }
    val searchMangaList: LiveData<List<Manga>> get() = _searchMangaList

    private val _searchAnimeList: MutableLiveData<List<Anime>> by
    lazy { MutableLiveData<List<Anime>>() }
    val searchAnimeList: LiveData<List<Anime>> get() = _searchAnimeList

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
        val searchAnime =
            actionStateFlow.filterIsInstance<SearchUiAction.SearchAnime>().distinctUntilChanged()
        val searchManga =
            actionStateFlow.filterIsInstance<SearchUiAction.SearchManga>().distinctUntilChanged()

        viewModelScope.launch {
            searchAnime.collectLatest {
                searchAnime(it.query)
            }
            searchManga.collectLatest {
                searchManga(it.query)
            }
        }

        searchUiState = combine(
            searchAnime,
            searchManga,
            ::Pair
        ).map { (anime, manga) ->
            SearchUiState(
                animeQuery = anime.query,
                mangaQuery = manga.query
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(1000L),
                initialValue = SearchUiState()
            )

        searchUiAction = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun searchAnime(title: String) {
        viewModelScope.launch {
            repo.searchAnime(query = title).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _searchError.postValue(result.message)
                    }
                    is Resource.Loading -> {
                        _searchLoading.postValue(result.isLoading)
                    }
                    is Resource.Success -> {
                        _searchAnimeList.postValue(result.data!!)
                    }
                }
            }
        }
    }

    private fun searchManga(title: String) {
        viewModelScope.launch {
            repo.searchManga(query = title).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        _searchError.postValue(result.message)
                    }
                    is Resource.Loading -> {
                        _searchLoading.postValue(result.isLoading)
                    }
                    is Resource.Success -> {
                        _searchMangaList.postValue(result.data!!)
                    }
                }
            }
        }
    }

}

sealed class SearchUiAction {
    data class SearchAnime(val query: String) : SearchUiAction()
    data class SearchManga(val query: String) : SearchUiAction()
}

data class SearchUiState(
    val animeQuery: String? = null,
    val mangaQuery: String? = null
)