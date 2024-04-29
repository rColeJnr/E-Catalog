package com.rick.anime.data_anime.data.repository.manga

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rick.data.database_anime.model.asManga
import com.rick.data.model_anime.UserManga
import com.rick.data.model_anime.mapToUserManga
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * implements all manga repositories and provides all favorites
 * */
class CompositeMangaRepository @Inject constructor(
    private val mangaRepository: MangaRepository,
    private val userDataRepository: UserMangaDataRepository
) : UserMangaRepository {
    override fun observeManga(viewModelScope: CoroutineScope): Flow<PagingData<UserManga>> =
        mangaRepository.getMangas().cachedIn(viewModelScope)
            .combine(userDataRepository.mangaUserData) { manga, userData ->
                manga.map { it.asManga().mapToUserManga(userData) }
            }

    override fun observeMangaFavorite(): Flow<List<UserManga>> =
        userDataRepository.mangaUserData.map { it.mangaFavoriteIds }.distinctUntilChanged()
            .flatMapLatest { favoriteIds ->
                when {
                    favoriteIds.isEmpty() -> flowOf(emptyList())
                    else -> mangaRepository.getMangaFavorites(favoriteIds)
                        .combine(userDataRepository.mangaUserData) { mangas, userData ->
                            mangas.map { it.mapToUserManga(userData) }
                        }
                }
            }

    override fun searchManga(query: String): Flow<List<UserManga>> =
        mangaRepository.searchManga(query = query)
            .combine(userDataRepository.mangaUserData) { mangas, userData ->
                mangas.map { it.mapToUserManga(userData) }
            }
}
