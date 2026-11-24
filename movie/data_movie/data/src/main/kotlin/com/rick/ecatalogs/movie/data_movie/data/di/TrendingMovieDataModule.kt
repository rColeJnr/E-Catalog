package com.rick.ecatalogs.movie.data_movie.data.di

import com.rick.ecatalogs.movie.data_movie.data.repository.browse.ReleaseReminderRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.browse.ReleaseReminderRepositoryImpl
import com.rick.ecatalogs.movie.data_movie.data.repository.browse.TmdbBrowseRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.browse.TmdbBrowseRepositoryImpl
import com.rick.ecatalogs.movie.data_movie.data.repository.person.PersonRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.person.PersonRepositoryImpl
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie.TrendingMovieByIdRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie.TrendingMovieByIdRepositoryImpl
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie.TrendingMovieRecentSearchRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie.TrendingMovieRecentSearchRepositoryImpl
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie.TrendingMovieRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie.TrendingMovieRepositoryImpl
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie.UserTrendingMovieDataRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie.UserTrendingMovieDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class TrendingMovieDataModule {

    @Binds
    internal abstract fun bindsUserTrendingMovieDataRepository(
        userDataRepository: UserTrendingMovieDataRepositoryImpl
    ): UserTrendingMovieDataRepository

    @Binds
    internal abstract fun bindsTrendingMovieRepository(
        trendingMovieRepository: TrendingMovieRepositoryImpl
    ): TrendingMovieRepository

    @Binds
    internal abstract fun bindsTrendingMovieRecentSearchRepository(
        trendingMovieRecentSearchRepository: TrendingMovieRecentSearchRepositoryImpl
    ): TrendingMovieRecentSearchRepository

    @Binds
    internal abstract fun bindsTrendingMovieByIdRepository(
        trendingMovieByIdRepository: TrendingMovieByIdRepositoryImpl
    ): TrendingMovieByIdRepository

    @Binds
    internal abstract fun bindsTmdbBrowseRepository(
        repository: TmdbBrowseRepositoryImpl
    ): TmdbBrowseRepository

    @Binds
    internal abstract fun bindsReleaseReminderRepository(
        repository: ReleaseReminderRepositoryImpl
    ): ReleaseReminderRepository

    @Binds
    internal abstract fun bindsPersonRepository(
        personRepository: PersonRepositoryImpl
    ): PersonRepository
}