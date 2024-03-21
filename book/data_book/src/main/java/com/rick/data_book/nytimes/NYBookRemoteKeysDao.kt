package com.rick.data_book.nytimes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NYBookRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<NYBookRemoteKeys>)

    @Query("SELECT * FROM ny_book_remote_keys WHERE id = :id")
    suspend fun remoteKeysId(id: Int): NYBookRemoteKeys?

    @Query("DELETE FROM ny_book_remote_keys")
    suspend fun clearRemoteKeys()

}