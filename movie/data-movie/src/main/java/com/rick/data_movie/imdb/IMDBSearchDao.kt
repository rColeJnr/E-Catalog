package com.rick.data_movie.imdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rick.data_movie.imdb.search_model.IMDBSearchResult

@Dao
interface IMDBSearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(result: List<IMDBSearchResult>)

    @Query(
        "SELECT * FROM imdb_search_result WHERE " +
                "title LIKE :queryString or description LIKE :queryString " +
                "ORDER BY title ASC"
    )
    fun movieByTitle(queryString: String): List<IMDBSearchResult>

    @Query(
        "SELECT * FROM imdb_search_result WHERE " +
                "title LIKE :queryString or description LIKE :queryString " +
                "ORDER BY title ASC"
    )
    fun seriesByTitle(queryString: String): List<IMDBSearchResult>

}