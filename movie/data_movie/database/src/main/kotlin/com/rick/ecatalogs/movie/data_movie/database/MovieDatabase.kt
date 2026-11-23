package com.rick.ecatalogs.movie.data_movie.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rick.ecatalogs.movie.data_movie.database.dao.ArticleDao
import com.rick.ecatalogs.movie.data_movie.database.dao.ArticleRecentSearchQueryDao
import com.rick.ecatalogs.movie.data_movie.database.dao.ArticleRemoteKeysDao
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingMovieDao
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingMovieRecentSearchQueryDao
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingMovieRemoteKeysDao
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingSeriesDao
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingSeriesRecentSearchQueryDao
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingSeriesRemoteKeysDao
import com.rick.ecatalogs.movie.data_movie.database.model.ArticleEntity
import com.rick.ecatalogs.movie.data_movie.database.model.ArticleRecentSearchQueryEntity
import com.rick.ecatalogs.movie.data_movie.database.model.ArticleRemoteKeys
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieEntity
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieRecentSearchQueryEntity
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieRemoteKeys
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingSeriesEntity
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingSeriesRecentSearchQueryEntity
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingSeriesRemoteKeys
import com.rick.ecatalogs.movie.data_movie.database.util.NyTimesConverters

@Database(
    entities = [
        ArticleEntity::class,
        ArticleRemoteKeys::class,
        TrendingMovieEntity::class,
        TrendingMovieRemoteKeys::class,
        TrendingSeriesEntity::class,
        TrendingSeriesRemoteKeys::class,
        ArticleRecentSearchQueryEntity::class,
        TrendingMovieRecentSearchQueryEntity::class,
        TrendingSeriesRecentSearchQueryEntity::class,
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    NyTimesConverters::class,
)
abstract class MovieDatabase : RoomDatabase() {
    abstract val articleDao: ArticleDao
    abstract val articleRemoteKeysDao: ArticleRemoteKeysDao
    abstract val trendingMovieRemoteKeysDao: TrendingMovieRemoteKeysDao
    abstract val trendingMovieDao: TrendingMovieDao
    abstract val trendingSeriesDao: TrendingSeriesDao
    abstract val trendingSeriesRemoteKeysDao: TrendingSeriesRemoteKeysDao
    abstract val articleRecentSearchQueryDao: ArticleRecentSearchQueryDao
    abstract val trendingMovieRecentSearchQueryDao: TrendingMovieRecentSearchQueryDao
    abstract val trendingSeriesRecentSearchQueryDao: TrendingSeriesRecentSearchQueryDao
}