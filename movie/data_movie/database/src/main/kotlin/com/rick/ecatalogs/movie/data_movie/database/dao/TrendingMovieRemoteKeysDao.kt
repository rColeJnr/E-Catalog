package com.rick.ecatalogs.movie.data_movie.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieRemoteKeys

@Dao
interface TrendingMovieRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<TrendingMovieRemoteKeys>)

    @Query("SELECT * FROM trending_movie_remote_keys WHERE id = :movie")
    suspend fun remoteKeysMovieId(movie: Int): TrendingMovieRemoteKeys?

    @Query("DELETE FROM trending_movie_remote_keys")
    suspend fun clearRemoteKeys()

    /** Ids of the movies on the first stored page (the page with no previous key). */
    @Query("SELECT id FROM trending_movie_remote_keys WHERE prev_key IS NULL")
    suspend fun firstPageIds(): List<Int>
}