package com.rick.data.data_book.di

import com.rick.data.data_book.repository.gutenberg.GutenbergRepository
import com.rick.data.data_book.repository.gutenberg.GutenbergRepositoryImpl
import com.rick.data.data_book.repository.gutenberg.UserGutenbergDataRepository
import com.rick.data.data_book.repository.gutenberg.UserGutenbergDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class GutenbergDataModule {

    @Binds
    internal abstract fun bindsUserGutenbergDataRepository(
        userDataRepository: UserGutenbergDataRepositoryImpl
    ): UserGutenbergDataRepository

    @Binds
    internal abstract fun bindsGutenbergRepository(
        gutenbergRepository: GutenbergRepositoryImpl
    ): GutenbergRepository

}