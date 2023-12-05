package com.rick.data_movie

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rick.data_movie.favorite.Favorite
import com.rick.data_movie.favorite.FavoriteDao
import com.rick.data_movie.ny_times.ArticleDao
import com.rick.data_movie.ny_times.RemoteKeys
import com.rick.data_movie.ny_times.RemoteKeysDao
import com.rick.data_movie.ny_times.article_models.Converters
import com.rick.data_movie.ny_times.article_models.NYMovie
import com.rick.data_movie.tmdb.TMDBConverters
import com.rick.data_movie.tmdb.TMDBDao
import com.rick.data_movie.tmdb.movie.MovieResponse
import com.rick.data_movie.tmdb.trending_movie.MovieRemoteKeys
import com.rick.data_movie.tmdb.trending_movie.MovieRemoteKeysDao
import com.rick.data_movie.tmdb.trending_movie.TrendingMovie
import com.rick.data_movie.tmdb.trending_tv.TrendingTv
import com.rick.data_movie.tmdb.trending_tv.TvRemoteKeys
import com.rick.data_movie.tmdb.trending_tv.TvRemoteKeysDao
import com.rick.data_movie.tmdb.tv.TvResponse

@Database(
    entities = [
//        IMDBMovie::class,
//        IMDBSearchResult::class,
        Favorite::class,
//        IMDBFavorite::class,
//        TvSeries::class,
        NYMovie::class,
        RemoteKeys::class,
        TrendingMovie::class,
        MovieRemoteKeys::class,
        TrendingTv::class,
        TvRemoteKeys::class,
        MovieResponse::class,
        TvResponse::class,
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class, TMDBConverters::class)
abstract class MovieCatalogDatabase : RoomDatabase() {
//    abstract val seriesDao: TvSeriesDao
//    abstract val imdbMovieAndSeriesDao: IMDBMovieAndSeriesDao
//    abstract val imdbSearchDao: IMDBSearchDao
    abstract val articleDao: ArticleDao
    abstract val remoteKeysDao: RemoteKeysDao
    abstract val tmdbDao: TMDBDao
    abstract val movieRemoteKeysDao: MovieRemoteKeysDao
    abstract val tvRemoteKeysDao: TvRemoteKeysDao
    abstract val favoriteDao: FavoriteDao

    companion object {
        const val DATABASE_NAME = "TEST_MOVIE_DB"
    }
}