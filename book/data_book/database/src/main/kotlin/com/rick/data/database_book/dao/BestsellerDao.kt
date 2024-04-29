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

    /**
     * Deletes rows in the db matching the specified [ids]
     */
    @Query(
        value = """
            DELETE FROM bestseller_table
            WHERE id in (:ids)
        """,
    )
    suspend fun clearBestsellers(ids: List<String>)

    @Query("SELECT * FROM bestseller_table ORDER BY rank ASC")
    fun getBestsellers(): Flow<List<BestsellerEntity>>

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
