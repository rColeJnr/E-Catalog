package com.rick.data.data_book.repository.bestseller

import com.rick.data.model_book.bestseller.UserBestseller
import com.rick.data.model_book.bestseller.mapToUserBestseller
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
        apiKey: String,
        genre: String,
        date: String,
        viewModelScope: CoroutineScope
    ): Flow<List<UserBestseller>> =
        bestsellerRepository.getBestseller(apiKey = apiKey, bookGenre = genre, date = date)
            .combine(userDataRepository.userData) { book, userData ->
                book.map { it.mapToUserBestseller(userData) }
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
