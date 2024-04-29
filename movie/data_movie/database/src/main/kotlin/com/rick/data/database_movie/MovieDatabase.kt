package com.rick.data.database_movie

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rick.data.database_movie.dao.ArticleDao
import com.rick.data.database_movie.dao.ArticleRecentSearchQueryDao
import com.rick.data.database_movie.dao.ArticleRemoteKeysDao
import com.rick.data.database_movie.dao.TrendingMovieDao
import com.rick.data.database_movie.dao.TrendingMovieRecentSearchQueryDao
import com.rick.data.database_movie.dao.TrendingMovieRemoteKeysDao
import com.rick.data.database_movie.dao.TrendingSeriesDao
import com.rick.data.database_movie.dao.TrendingSeriesRecentSearchQueryDao
import com.rick.data.database_movie.dao.TrendingSeriesRemoteKeysDao
import com.rick.data.database_movie.model.ArticleEntity
import com.rick.data.database_movie.model.ArticleRecentSearchQueryEntity
import com.rick.data.database_movie.model.ArticleRemoteKeys
import com.rick.data.database_movie.model.TrendingMovieEntity
import com.rick.data.database_movie.model.TrendingMovieRecentSearchQueryEntity
import com.rick.data.database_movie.model.TrendingMovieRemoteKeys
import com.rick.data.database_movie.model.TrendingSeriesEntity
import com.rick.data.database_movie.model.TrendingSeriesRecentSearchQueryEntity
import com.rick.data.database_movie.model.TrendingSeriesRemoteKeys
import com.rick.data.database_movie.util.NyTimesConverters

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