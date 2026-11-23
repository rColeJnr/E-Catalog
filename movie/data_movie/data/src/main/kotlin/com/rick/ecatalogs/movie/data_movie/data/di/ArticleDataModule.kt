package com.rick.ecatalogs.movie.data_movie.data.di

import com.rick.ecatalogs.movie.data_movie.data.repository.article.ArticleRecentSearchRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.article.ArticleRecentSearchRepositoryImpl
import com.rick.ecatalogs.movie.data_movie.data.repository.article.ArticleRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.article.ArticleRepositoryImpl
import com.rick.ecatalogs.movie.data_movie.data.repository.article.UserArticleDataRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.article.UserArticleDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ArticleDataModule {

    @Binds
    internal abstract fun bindsArticleUserDataRepository(
        userDataRepository: UserArticleDataRepositoryImpl
    ): UserArticleDataRepository

    @Binds
    internal abstract fun bindsArticleRepository(
        articleRepository: ArticleRepositoryImpl
    ): ArticleRepository

    @Binds
    internal abstract fun bindsArticleRecentSearchRepository(
        articleRecentSearchRepository: ArticleRecentSearchRepositoryImpl
    ): ArticleRecentSearchRepository
}