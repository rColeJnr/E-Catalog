package com.rick.data.movie_favorite.di

import com.rick.data.movie_favorite.repository.article.ArticleRepository
import com.rick.data.movie_favorite.repository.article.ArticleRepositoryImpl
import com.rick.data.movie_favorite.repository.article.UserArticleDataRepository
import com.rick.data.movie_favorite.repository.article.UserArticleDataRepositoryImpl
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

}