package com.rick.data.database_book.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data.database_book.model.BestsellerRemoteKeys

@Dao
interface BestsellerRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<BestsellerRemoteKeys>)

    @Query("SELECT * FROM ny_book_remote_keys WHERE id = :id")
    suspend fun remoteKeysId(id: String): BestsellerRemoteKeys?

    @Query("DELETE FROM ny_book_remote_keys")
    suspend fun clearRemoteKeys()

}