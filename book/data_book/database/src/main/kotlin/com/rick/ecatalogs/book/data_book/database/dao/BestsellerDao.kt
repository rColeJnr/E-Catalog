package com.rick.ecatalogs.book.data_book.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.rick.ecatalogs.book.data_book.database.model.BestsellerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BestsellerDao {

    @Upsert
    suspend fun upsertBestsellers(books: List<BestsellerEntity>)

    /** Deletes the stored list for one [genre] (NYT list name). */
    @Query("DELETE FROM bestseller_table WHERE genre = :genre")
    suspend fun clearBestsellers(genre: String)

    /** Replaces the stored list for one [genre] in a single transaction. */
    @Transaction
    suspend fun replaceBestsellers(genre: String, books: List<BestsellerEntity>) {
        clearBestsellers(genre)
        upsertBestsellers(books)
    }

    @Query("SELECT * FROM bestseller_table WHERE genre = :genre ORDER BY rank ASC")
    fun getBestsellers(genre: String): Flow<List<BestsellerEntity>>

    @Query("SELECT id FROM bestseller_table WHERE genre = :genre")
    suspend fun bestsellerIds(genre: String): List<String>

    @Query(
        """
            SELECT * FROM bestseller_table
            WHERE 
                CASE WHEN :filterById
                    THEN id IN (:filterIds)
                    ELSE 1
                END
            AND
                CASE WHEN :filterByQuery
                    THEN title LIKE (:query)
                    ELSE 1
                END
            GROUP BY id
            ORDER BY rank DESC
        """
    )
    fun getBestsellerWithFilters(
        filterById: Boolean = false,
        filterIds: Set<String> = emptySet(),
        filterByQuery: Boolean = false,
        query: String = ""
    ): Flow<List<BestsellerEntity>>

}
