package com.rick.data.data_book.di

import com.rick.data.data_book.repository.bestseller.CompositeBestsellerRepository
import com.rick.data.data_book.repository.bestseller.UserBestsellerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface BestsellerUserCompositeRepository {

    @Binds
    fun bindsUserBookRepository(
        compositeRepository: CompositeBestsellerRepository
    ): UserBestsellerRepository

}