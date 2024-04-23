package com.rick.data.database_book.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rick.data.database_book.model.BestsellerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BestsellerDao {

    @Upsert
    suspend fun upsertBestsellers(books: List<BestsellerEntity>)

    @Query("DELETE FROM bestseller_table")
    suspend fun clearBestsellers()

    @Query("SELECT * FROM bestseller_table ORDER BY rank ASC")
    fun getBestsellers(): Flow<List<BestsellerEntity>>

    @Query(
        """
            SELECT * FROM bestseller_table
            WHERE id IN (:filterBestsellerIds)
            ORDER BY id
        """
    )
    fun getBestsellerWithFilters(
        filterBestsellerIds: Set<String>
    ): Flow<List<BestsellerEntity>>

}
