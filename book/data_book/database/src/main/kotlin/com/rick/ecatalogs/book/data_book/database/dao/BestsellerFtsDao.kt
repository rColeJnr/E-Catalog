package com.rick.ecatalogs.book.data_book.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.ecatalogs.book.data_book.database.model.BestsellerFtsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BestsellerFtsDao {
    @Upsert
    suspend fun upsertBestsellers(bestsellers: List<BestsellerFtsEntity>)

    @Query("SELECT bestsellerId FROM bestseller_fts_table WHERE bestseller_fts_table MATCH :query")
    fun searchAllBestsellers(query: String): Flow<List<String>>

    @Query("SELECT count(*) FROM bestseller_fts_table")
    fun getCount(): Flow<Int>
}
