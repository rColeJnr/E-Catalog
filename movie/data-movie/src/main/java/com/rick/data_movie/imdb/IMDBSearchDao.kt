package com.rick.data_movie.imdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_movie.imdb.search_model.SearchResult

@Dao
interface IMDBSearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(result: List<SearchResult>)

    @Query(
        "SELECT * FROM search_result WHERE " +
                "title LIKE :queryString or description LIKE :queryString " +
                "ORDER BY title ASC"
    )
    fun resultByTitle(queryString: String): List<SearchResult>

}