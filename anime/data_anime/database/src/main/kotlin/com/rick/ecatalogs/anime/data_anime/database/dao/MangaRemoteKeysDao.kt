package com.rick.ecatalogs.anime.data_anime.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.ecatalogs.anime.data_anime.database.model.MangaRemoteKeys

@Dao
interface MangaRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<MangaRemoteKeys>)

    @Query("SELECT * FROM manga_remote_keys WHERE manga = :manga")
    suspend fun remoteKeysId(manga: Int): MangaRemoteKeys?

    @Query("DELETE FROM manga_remote_keys")
    suspend fun clearRemoteKeys()

    /** Ids of the stored first page (the background sync rewrites it only when they change). */
    @Query("SELECT manga FROM manga_remote_keys WHERE prevKey IS NULL")
    suspend fun firstPageIds(): List<Int>
}