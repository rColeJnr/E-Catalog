package com.rick.data_movie

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rick.data_movie.favorite.FavDao
import com.rick.data_movie.favorite.Favorite
import com.rick.data_movie.favorite.IMDBFavorite
import com.rick.data_movie.imdb.IMDBConverters
import com.rick.data_movie.imdb.IMDBMovieAndSeriesDao
import com.rick.data_movie.imdb.IMDBSearchDao
import com.rick.data_movie.imdb.TvSeriesDao
import com.rick.data_movie.imdb.movie_model.IMDBMovie
import com.rick.data_movie.imdb.search_model.IMDBSearchResult
import com.rick.data_movie.imdb.series_model.TvSeries
import com.rick.data_movie.ny_times.ArticleDao
import com.rick.data_movie.ny_times.RemoteKeys
import com.rick.data_movie.ny_times.RemoteKeysDao
import com.rick.data_movie.ny_times.article_models.Converters
import com.rick.data_movie.ny_times.article_models.Doc

@Database(
    entities = [
        IMDBMovie::class,
        IMDBSearchResult::class,
        Favorite::class,
        IMDBFavorite::class,
        RemoteKeys::class,
        TvSeries::class,
        Doc::class,
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class, IMDBConverters::class)
abstract class MovieCatalogDatabase: RoomDatabase() {
    abstract val seriesDao: TvSeriesDao
    abstract val imdbMovieAndSeriesDao: IMDBMovieAndSeriesDao
    abstract val imdbSearchDao: IMDBSearchDao
    abstract val articleDao: ArticleDao
    abstract val favDao: FavDao
    abstract val remoteKeysDao: RemoteKeysDao

    companion object {
        const val DATABASE_NAME = "TEST_DB"
    }
}