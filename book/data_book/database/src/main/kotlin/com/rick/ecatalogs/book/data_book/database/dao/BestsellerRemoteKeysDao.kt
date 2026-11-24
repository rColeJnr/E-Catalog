package com.rick.ecatalogs.book.data_book.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.ecatalogs.book.data_book.database.model.BestsellerRemoteKeys

@Dao
interface BestsellerRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<BestsellerRemoteKeys>)

    @Query("SELECT * FROM ny_book_remote_keys WHERE id = :id")
    suspend fun remoteKeysId(id: String): BestsellerRemoteKeys?

    @Query("DELETE FROM ny_book_remote_keys")
    suspend fun clearRemoteKeys()

    /** Keys are "genre|isbn" (a book can be on several lists); clears one genre's keys. */
    @Query("DELETE FROM ny_book_remote_keys WHERE id LIKE :genre || '|%'")
    suspend fun clearRemoteKeys(genre: String)

}