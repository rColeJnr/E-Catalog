package com.rick.data_movie.tmdb.trending_tv

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TvRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<TvRemoteKeys>)

    @Query("SELECT * FROM tv_remote_keys WHERE id = :id")
    suspend fun remoteKeysId(id: Int): TvRemoteKeys?

    @Query("DELETE FROM tv_remote_keys")
    suspend fun clearRemoteKeys()

}