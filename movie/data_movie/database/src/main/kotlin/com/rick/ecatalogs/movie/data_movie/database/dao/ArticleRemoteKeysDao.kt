package com.rick.ecatalogs.movie.data_movie.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.ecatalogs.movie.data_movie.database.model.ArticleRemoteKeys

@Dao
interface ArticleRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<ArticleRemoteKeys>)

    @Query("SELECT * FROM article_remote_keys_table WHERE id = :id")
    suspend fun remoteKeysId(id: String): ArticleRemoteKeys?

    @Query("DELETE FROM article_remote_keys_table")
    suspend fun clearRemoteKeys()

}