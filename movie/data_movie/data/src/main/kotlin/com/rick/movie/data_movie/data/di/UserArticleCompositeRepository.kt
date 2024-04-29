package com.rick.movie.data_movie.data.di

import com.rick.movie.data_movie.data.repository.article.CompositeArticleRepository
import com.rick.movie.data_movie.data.repository.article.UserArticlesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
internal interface UserArticleCompositeRepository {

    @Binds
    fun bindsUserArticleRepository(
        compositeRepository: CompositeArticleRepository
    ): UserArticlesRepository

}