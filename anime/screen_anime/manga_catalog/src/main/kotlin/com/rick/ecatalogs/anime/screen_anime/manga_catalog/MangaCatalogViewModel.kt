package com.rick.ecatalogs.anime.screen_anime.manga_catalog

import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.catch
import com.rick.ecatalogs.anime.data_anime.model.browse.JikanItem
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.rick.ecatalogs.anime.data_anime.model.browse.JikanGenre
import com.rick.ecatalogs.anime.data_anime.model.browse.MangaList
import com.rick.ecatalogs.anime.data_anime.data.repository.browse.JikanBrowseRepository
import androidx.paging.map
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rick.ecatalogs.anime.screen_anime.common.JikanUiEvents
import com.rick.ecatalogs.anime.data_anime.data.repository.manga.CompositeMangaRepository
import com.rick.ecatalogs.anime.data_anime.data.repository.manga.UserMangaDataRepository
import com.rick.ecatalogs.anime.data_anime.model.UserManga
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MangaCatalogViewModel @Inject constructor(
    private val userDataRepository: UserMangaDataRepository,
    private val compositeMangaRepository: CompositeMangaRepository,
    private val browseRepository: JikanBrowseRepository,
) : ViewModel() {

    /** Which list the catalog shows; [MangaList.POPULAR] with no genre is the stored list. */
    private val _list = MutableStateFlow(MangaList.POPULAR)
    val selectedList: StateFlow<MangaList> = _list.asStateFlow()

    /** A Jikan genre id, or null for all genres. */
    private val _genre = MutableStateFlow<Int?>(null)
    val selectedGenre: StateFlow<Int?> = _genre.asStateFlow()

    private val stored = compositeMangaRepository.observeManga(viewModelScope).cachedIn(viewModelScope)

    val pagingDataFlow: Flow<PagingData<UserManga>> = combine(_list, _genre) { list, genre -> list to genre }
        .flatMapLatest { (list, genre) ->
            if (list == MangaList.POPULAR && genre == null) {
                stored
            } else {
                browseRepository.browseManga(list, genre)
                    .cachedIn(viewModelScope)
                    .combine(userDataRepository.mangaUserData) { items, userData ->
                        items.map { UserManga(it, userData) }
                    }
            }
        }
        .cachedIn(viewModelScope)

    val genres: StateFlow<List<JikanGenre>> = flow { emit(browseRepository.mangaGenres()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** The featured row above the list (cached, see [JikanBrowseRepository.featuredManga]). */
    val publishingNow: StateFlow<List<JikanItem>> = browseRepository.featuredManga(MangaList.PUBLISHING)
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectList(list: MangaList) {
        _list.value = list
    }

    /** "Surprise me": a random (safe for work) manga id, or null if none could be picked. */
    suspend fun randomMangaId(): Int? = runCatching { browseRepository.randomMangaId() }.getOrNull()

    fun selectGenre(genreId: Int?) {
        _genre.value = genreId
    }

    private fun fetchManga(): Flow<PagingData<UserManga>> =
        compositeMangaRepository.observeManga(viewModelScope)

    fun onEvent(event: JikanUiEvents) {
        when (event) {
            is JikanUiEvents.UpdateMangaFavorite -> updateMangaFavorite(
                id = event.id,
                event.isFavorite
            )

            else -> {}
        }
    }

    private fun updateMangaFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataRepository.setMangaFavoriteId(id, isFavorite)
        }
    }

}