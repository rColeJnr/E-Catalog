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
                "searchType LIKE :searchType and title " +
                "LIKE :queryString or description LIKE :queryString " +
                "ORDER BY title ASC"
    )
    fun movieByTitle(queryString: String, searchType: String = MOVIE): List<IMDBSearchResult>

    @Query(
        "SELECT * FROM imdb_search_result WHERE " +
                "searchType LIKE :searchType and " +
                "title LIKE :queryString or description LIKE :queryString " +
                "ORDER BY title ASC"
    )
    fun seriesByTitle(queryString: String, searchType: String = SERIES): List<IMDBSearchResult>

}

private const val MOVIE = "Movie"
private const val SERIES = "Series"
