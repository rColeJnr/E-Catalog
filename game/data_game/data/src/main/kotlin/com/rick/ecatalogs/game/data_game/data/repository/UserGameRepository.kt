package com.rick.ecatalogs.game.data_game.data.repository

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rick.ecatalogs.data.analytics.AnalyticsEvent
import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.game.data_game.model.Game
import com.rick.ecatalogs.game.data_game.model.GameDetails
import com.rick.ecatalogs.game.data_game.model.GameUserData
import com.rick.ecatalogs.game.data_game.model.UserGame
import com.rick.ecatalogs.game.data_game.model.UserGameDetails
import com.rick.ecatalogs.game.data_game.model.asUserGame
import com.rick.ecatalogs.game.data_game.model.asUserGames
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface UserGameDataRepository {
    val gameUserData: Flow<GameUserData>
    suspend fun setGameFavorite(gameId: Int, isFavorite: Boolean)
}

internal class UserGameDataRepositoryImpl @Inject constructor(
    private val preferences: PreferencesDataSource,
    private val analyticsHelper: AnalyticsHelper,
) : UserGameDataRepository {
    override val gameUserData: Flow<GameUserData> get() = preferences.gameUserData

    override suspend fun setGameFavorite(gameId: Int, isFavorite: Boolean) {
        preferences.setGameFavoriteIds(gameId, isFavorite)
        analyticsHelper.logEvent(
            AnalyticsEvent(
                type = if (isFavorite) "game_saved" else "game_unsaved",
                extras = listOf(
                    AnalyticsEvent.Param(if (isFavorite) "saved_game_id" else "unsaved_game_id", gameId.toString()),
                ),
            ),
        )
    }
}

/** Games joined with the user's favorites. */
interface UserGameRepository {
    fun observePopularGames(scope: CoroutineScope): Flow<PagingData<UserGame>>
    fun observeFavoriteGames(): Flow<List<UserGame>>
    fun observeSearch(query: String): Flow<List<UserGame>>
    fun observeDetails(details: GameDetails): Flow<UserGameDetails>
    fun withFavorites(games: Flow<PagingData<Game>>): Flow<PagingData<UserGame>>
}

@OptIn(ExperimentalCoroutinesApi::class)
class CompositeGameRepository @Inject constructor(
    private val gameRepository: GameRepository,
    private val userDataRepository: UserGameDataRepository,
) : UserGameRepository {

    override fun observePopularGames(scope: CoroutineScope): Flow<PagingData<UserGame>> =
        withFavorites(gameRepository.getPopularGames().cachedIn(scope))

    override fun withFavorites(games: Flow<PagingData<Game>>): Flow<PagingData<UserGame>> =
        games.combine(userDataRepository.gameUserData) { page, userData ->
            page.map { it.asUserGame(userData) }
        }

    override fun observeFavoriteGames(): Flow<List<UserGame>> =
        userDataRepository.gameUserData.map { it.gameFavoriteIds }.distinctUntilChanged()
            .flatMapLatest { ids ->
                if (ids.isEmpty()) flowOf(emptyList())
                else gameRepository.getGames(ids)
                    .combine(userDataRepository.gameUserData) { games, userData -> games.asUserGames(userData) }
            }

    override fun observeSearch(query: String): Flow<List<UserGame>> =
        gameRepository.searchGames(query)
            .combine(userDataRepository.gameUserData) { games, userData -> games.asUserGames(userData) }

    override fun observeDetails(details: GameDetails): Flow<UserGameDetails> =
        userDataRepository.gameUserData.map { UserGameDetails(details, details.game.id in it.gameFavoriteIds) }
}
