package com.rick.data_movie

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieCatalogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieCatalog(movieCatalogEntitiy: MovieCatalogEntitiy)

    @Query("DELETE FROM moviecatalogentitiy")
    suspend fun clearMovieCatalogEntities()

    @Query("SELECT * FROM moviecatalogentitiy")
    suspend fun getMovieCatalog(): List<MovieCatalogEntitiy>

}