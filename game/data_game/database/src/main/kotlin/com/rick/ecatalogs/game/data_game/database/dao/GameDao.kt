package com.rick.ecatalogs.game.data_game.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.rick.ecatalogs.game.data_game.database.model.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    /** Rows that aren't part of the catalog list (search results, details); never replaces a stored row. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOutsideCatalog(entities: List<GameEntity>)

    /** Takes every row out of the catalog list, before a refresh writes the list again. */
    @Query("UPDATE game_table SET catalog_position = NULL")
    suspend fun clearCatalog()

    /** Deletes the rows that are neither in the catalog list nor in [keepIds] (the favorites). */
    @Query("DELETE FROM game_table WHERE catalog_position IS NULL AND id NOT IN (:keepIds)")
    suspend fun deleteOutsideCatalog(keepIds: Set<Int>)

    @Upsert
    suspend fun upsertGames(games: List<GameEntity>)

    // Only the catalog list: search results and kept favorites have no position
    @Query("SELECT * FROM game_table WHERE catalog_position IS NOT NULL ORDER BY catalog_position")
    fun getGames(): PagingSource<Int, GameEntity>

    @Query("SELECT * FROM game_table WHERE id IN (:ids) ORDER BY name COLLATE NOCASE")
    fun getGamesByIds(ids: Set<Int>): Flow<List<GameEntity>>

    @Query("SELECT * FROM game_table WHERE name LIKE :query ORDER BY rating_count DESC")
    fun searchGames(query: String): Flow<List<GameEntity>>
}
