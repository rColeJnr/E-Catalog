package com.rick.ecatalogs.game.data_game.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.ecatalogs.game.data_game.database.model.GameRemoteKeys

@Dao
interface GameRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<GameRemoteKeys>)

    @Query("SELECT * FROM game_remote_keys WHERE game = :game")
    suspend fun remoteKeysId(game: Int): GameRemoteKeys?

    @Query("DELETE FROM game_remote_keys")
    suspend fun clearRemoteKeys()

    /** Ids of the stored first page (the background sync rewrites it only when they change). */
    @Query("SELECT game FROM game_remote_keys WHERE prevKey IS NULL")
    suspend fun firstPageIds(): List<Int>
}
