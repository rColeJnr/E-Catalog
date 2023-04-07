package com.rick.data_movie

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rick.data_movie.favorite.FavDao
import com.rick.data_movie.favorite.Favorite
import com.rick.data_movie.imdb.IMDBConverters
import com.rick.data_movie.imdb.IMDBMovieAndSeriesDao
import com.rick.data_movie.imdb.IMDBSearchDao
import com.rick.data_movie.imdb.TvSeriesDao
import com.rick.data_movie.imdb.movie_model.IMDBMovie
import com.rick.data_movie.imdb.search_model.IMDBSearchResult
import com.rick.data_movie.imdb.series_model.TvSeries
import com.rick.data_movie.ny_times.Converters
import com.rick.data_movie.ny_times.Movie

@Database(
    entities = [
        Movie::class,
        IMDBMovie::class,
        IMDBSearchResult::class,
        Favorite::class,
        RemoteKeys::class,
        TvSeries::class,
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class, IMDBConverters::class)
abstract class MovieCatalogDatabase: RoomDatabase() {
    abstract val seriesDao: TvSeriesDao
    abstract val imdbMovieAndSeriesDao: IMDBMovieAndSeriesDao
    abstract val imdbSearchDao: IMDBSearchDao
    abstract val moviesDao: MoviesDao
    abstract val favDao: FavDao
    abstract val remoteKeysDao: RemoteKeysDao

    companion object {
        const val DATABASE_NAME = "MOVIE_DB"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE movies_db ADD COLUMN favorite BOOLEAN")
            }
        }
    }
}