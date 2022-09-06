package com.rick.data_movie

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rick.data_movie.imdb.IMDBMovieAndSeriesDao
import com.rick.data_movie.imdb.IMDBSearchDao
import com.rick.data_movie.imdb.movie_model.IMDBMovie
import com.rick.data_movie.imdb.search_model.IMDBSearchResult
import com.rick.data_movie.ny_times.Movie

@Database(
    entities = [Movie::class, IMDBMovie::class, IMDBSearchResult::class, RemoteKeys::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class, IMDBConverters::class)
abstract class MovieCatalogDatabase: RoomDatabase() {
    abstract val imdbMovieAndSeriesDao: IMDBMovieAndSeriesDao
    abstract val imdbSearchDao: IMDBSearchDao
    abstract val moviesDao: MoviesDao
    abstract val remoteKeysDao: RemoteKeysDao

    companion object {
        const val DATABASE_NAME = "MOVIE_DB"
    }
}