package com.rick.ecatalogs.anime.data_anime.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.ecatalogs.anime.data_anime.database.model.AnimeRemoteKeys

@Dao
interface AnimeRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<AnimeRemoteKeys>)

    @Query("SELECT * FROM anime_remote_keys WHERE anime = :anime")
    suspend fun remoteKeysId(anime: Int): AnimeRemoteKeys?

    @Query("DELETE FROM anime_remote_keys")
    suspend fun clearRemoteKeys()

}