package com.rick.ecatalogs.book.data_book.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.ecatalogs.book.data_book.database.model.GutenbergRemoteKeys

@Dao
interface GutenbergRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<GutenbergRemoteKeys>)

    @Query("SELECT * FROM book_remote_keys WHERE book = :book")
    suspend fun remoteKeysId(book: Int): GutenbergRemoteKeys?

    @Query("DELETE FROM book_remote_keys")
    suspend fun clearRemoteKeys()
}