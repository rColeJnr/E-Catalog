package com.rick.ecatalogs.game.data_game.data.repository

import com.rick.ecatalogs.game.data_game.database.dao.GameRecentSearchQueryDao
import com.rick.ecatalogs.game.data_game.database.model.GameRecentSearchQueryEntity
import com.rick.ecatalogs.game.data_game.database.model.asExternalModel
import com.rick.ecatalogs.game.data_game.model.GameRecentSearchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import javax.inject.Inject

interface GameRecentSearchRepository {
    fun getRecentSearchQueries(limit: Int = 10): Flow<List<GameRecentSearchQuery>>
    suspend fun insertOrReplaceRecentSearch(query: String)
    suspend fun clearRecentSearches()
}

internal class GameRecentSearchRepositoryImpl @Inject constructor(
    private val dao: GameRecentSearchQueryDao,
) : GameRecentSearchRepository {
    override fun getRecentSearchQueries(limit: Int): Flow<List<GameRecentSearchQuery>> =
        dao.getRecentSearchQueries(limit).map { queries -> queries.map { it.asExternalModel() } }

    override suspend fun insertOrReplaceRecentSearch(query: String) =
        dao.insertOrReplaceRecentSearchQuery(GameRecentSearchQueryEntity(query, Clock.System.now()))

    override suspend fun clearRecentSearches() = dao.clearRecentSearchQueries()
}
