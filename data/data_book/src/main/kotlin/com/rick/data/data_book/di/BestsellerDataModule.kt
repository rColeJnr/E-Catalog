package com.rick.data.data_book.di

import com.rick.data.data_book.repository.bestseller.BestsellerRepository
import com.rick.data.data_book.repository.bestseller.BestsellerRepositoryImpl
import com.rick.data.data_book.repository.bestseller.UserBestsellerDataRepository
import com.rick.data.data_book.repository.bestseller.UserBestsellerDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BestsellerDataModule {

    @Binds
    internal abstract fun bindsUserBestsellerDataRepository(
        userDataRepository: UserBestsellerDataRepositoryImpl
    ): UserBestsellerDataRepository

    @Binds
    internal abstract fun bindsBestsellerRepository(
        bestsellerRepository: BestsellerRepositoryImpl
    ): BestsellerRepository

}