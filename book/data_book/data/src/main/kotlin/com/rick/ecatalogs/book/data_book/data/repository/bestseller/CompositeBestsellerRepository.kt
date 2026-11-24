package com.rick.ecatalogs.book.data_book.data.repository.bestseller

import androidx.paging.map
import androidx.paging.cachedIn
import androidx.paging.PagingData
import com.rick.ecatalogs.book.data_book.model.bestseller.UserBestseller
import com.rick.ecatalogs.book.data_book.model.bestseller.mapToUserBestseller
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
class CompositeBestsellerRepository @Inject constructor(
    private val bestsellerRepository: BestsellerRepository,
    private val userDataRepository: UserBestsellerDataRepository
) : UserBestsellerRepository {
    override fun observeBestseller(
        genre: String,
        date: String,
        viewModelScope: CoroutineScope
    ): Flow<List<UserBestseller>> =
        bestsellerRepository.getBestseller(bookGenre = genre, date = date)
            .combine(userDataRepository.userData) { book, userData ->
                book.map { it.mapToUserBestseller(userData) }
            }

    override fun observeBestsellerPaging(
        genre: String,
        scope: CoroutineScope,
    ): Flow<PagingData<UserBestseller>> =
        // cachedIn first: favorite changes re-map the same PagingData, which may only be
        // collected once otherwise
        bestsellerRepository.getBestsellerPaging(genre)
            .cachedIn(scope)
            .combine(userDataRepository.userData) { books, userData ->
                books.map { it.mapToUserBestseller(userData) }
            }

    override fun observeBestsellerById(id: String): Flow<UserBestseller?> =
        bestsellerRepository.getBestsellerById(id)
            .combine(userDataRepository.userData) { book, userData ->
                book?.mapToUserBestseller(userData)
            }

    override fun observeBestsellerFavorite(): Flow<List<UserBestseller>> =
        userDataRepository.userData.map { it.bestsellerFavoriteIds }.distinctUntilChanged()
            .flatMapLatest { favoriteIds ->
                when {
                    favoriteIds.isEmpty() -> flowOf(emptyList())
                    else -> bestsellerRepository.getBestsellerFavorites(favoriteIds)
                        .combine(userDataRepository.userData) { book, userData ->
                            book.map { it.mapToUserBestseller(userData) }
                        }
                }
            }

}
