package com.rick.data.database_movie.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data.database_movie.model.TrendingMovieRemoteKeys

@Dao
interface TrendingMovieRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<TrendingMovieRemoteKeys>)

    @Query("SELECT * FROM trending_movie_remote_keys WHERE id = :movie")
    suspend fun remoteKeysMovieId(movie: Int): TrendingMovieRemoteKeys?

    @Query("DELETE FROM trending_movie_remote_keys")
    suspend fun clearRemoteKeys()
}