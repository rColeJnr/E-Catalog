package com.rick.data_book.gutenberg

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<BookRemoteKeys>)

    @Query("SELECT * FROM book_remote_keys WHERE book = :book")
    suspend fun remoteKeysId(book: Int): BookRemoteKeys?

    @Query("DELETE FROM book_remote_keys")
    suspend fun clearRemoteKeys()
}