package com.rick.ecatalogs.movie.data_movie.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingSeriesRemoteKeys

@Dao
interface TrendingSeriesRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<TrendingSeriesRemoteKeys>)

    @Query("SELECT * FROM trending_series_remote_keys WHERE id = :id")
    suspend fun remoteKeysId(id: Int): TrendingSeriesRemoteKeys?

    @Query("DELETE FROM trending_series_remote_keys")
    suspend fun clearRemoteKeys()

}