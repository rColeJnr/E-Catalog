package com.rick.data.data_book.repository.gutenberg

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rick.data.database_book.model.asGutenberg
import com.rick.data.model_book.gutenberg.UserGutenberg
import com.rick.data.model_book.gutenberg.mapToUserGutenberg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * implements all repositories and provides all favorites
 * */
class CompositeGutenbergRepository @Inject constructor(
    private val gutenbergRepository: GutenbergRepository,
    private val userDataRepository: UserGutenbergDataRepository
) : UserGutenbergRepository {

    override fun observeGutenberg(scope: CoroutineScope): Flow<PagingData<UserGutenberg>> =
        gutenbergRepository.getGutenberg().cachedIn(scope)
            .combine(userDataRepository.userData) { book, userData ->
                book.map { it.asGutenberg().mapToUserGutenberg(userData) }
            }

    override fun observeGutenbergFavorite(): Flow<List<UserGutenberg>> =
        userDataRepository.userData.map { it.gutenbergFavoriteIds }.distinctUntilChanged()
            .flatMapLatest { gutenbergFavorites ->
                when {
                    gutenbergFavorites.isEmpty() -> flowOf(emptyList())
                    else -> {
                        gutenbergRepository.getGutenbergFavorites(gutenbergFavorites)
                            .combine(userDataRepository.userData) { gutenbergs, userData ->
                                gutenbergs.mapToUserGutenberg(userData)
                            }
                    }
                }
            }

    override fun searchGutenbergs(query: String): Flow<List<UserGutenberg>> =
        gutenbergRepository.searchGutenberg(query = query)
            .combine(userDataRepository.userData) { gutenbergs, userData ->
                gutenbergs.map { it.mapToUserGutenberg(userData) }
            }
}
