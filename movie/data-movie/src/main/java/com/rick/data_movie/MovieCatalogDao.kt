package com.rick.data_movie

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieCatalogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieCatalog(movieCatalogEntity: MovieCatalogEntity)

    @Query("DELETE FROM moviecatalogentity")
    suspend fun clearMovieCatalogEntities()

    @Query("SELECT * FROM moviecatalogentity")
    suspend fun getMovieCatalog(): List<MovieCatalogEntity>

}