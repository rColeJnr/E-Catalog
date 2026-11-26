package com.rick.ecatalogs.game.data_game.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.ecatalogs.game.data_game.database.model.GameRecentSearchQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameRecentSearchQueryDao {
    @Query("SELECT * FROM game_recent_search_queries_table ORDER BY queriedDate DESC LIMIT :limit")
    fun getRecentSearchQueries(limit: Int): Flow<List<GameRecentSearchQueryEntity>>

    @Upsert
    suspend fun insertOrReplaceRecentSearchQuery(query: GameRecentSearchQueryEntity)

    @Query("DELETE FROM game_recent_search_queries_table")
    suspend fun clearRecentSearchQueries()
}
