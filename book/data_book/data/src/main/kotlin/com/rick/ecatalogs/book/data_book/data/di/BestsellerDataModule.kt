package com.rick.ecatalogs.book.data_book.data.di

import com.rick.ecatalogs.book.data_book.data.repository.bestseller.BestsellerRepository
import com.rick.ecatalogs.book.data_book.data.repository.bestseller.BestsellerRepositoryImpl
import com.rick.ecatalogs.book.data_book.data.repository.bestseller.UserBestsellerDataRepository
import com.rick.ecatalogs.book.data_book.data.repository.bestseller.UserBestsellerDataRepositoryImpl
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