package com.rick.data_movie

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [MovieCatalogEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MovieCatalogDatabase: RoomDatabase() {
    abstract val dao: MovieCatalogDao

    companion object {
        const val DATABASE_NAME = "MOVIE_DB"
    }
}