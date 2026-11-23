package com.rick.ecatalogs.book.data_book.data.di

import com.rick.ecatalogs.book.data_book.data.repository.bestseller.CompositeBestsellerRepository
import com.rick.ecatalogs.book.data_book.data.repository.bestseller.UserBestsellerRepository
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