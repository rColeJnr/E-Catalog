package com.rick.data.database_anime.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data.database_anime.model.MangaRemoteKeys

@Dao
interface MangaRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<MangaRemoteKeys>)

    @Query("SELECT * FROM manga_remote_keys WHERE manga = :manga")
    suspend fun remoteKeysId(manga: Int): MangaRemoteKeys?

    @Query("DELETE FROM manga_remote_keys")
    suspend fun clearRemoteKeys()
}