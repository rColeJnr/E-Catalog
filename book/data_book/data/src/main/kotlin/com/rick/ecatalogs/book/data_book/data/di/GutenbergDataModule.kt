package com.rick.ecatalogs.book.data_book.data.di

import com.rick.ecatalogs.book.data_book.data.repository.gutenberg.GutenbergRecentSearchRepository
import com.rick.ecatalogs.book.data_book.data.repository.gutenberg.GutenbergRecentSearchRepositoryImpl
import com.rick.ecatalogs.book.data_book.data.repository.gutenberg.GutenbergRepository
import com.rick.ecatalogs.book.data_book.data.repository.gutenberg.GutenbergRepositoryImpl
import com.rick.ecatalogs.book.data_book.data.repository.gutenberg.UserGutenbergDataRepository
import com.rick.ecatalogs.book.data_book.data.repository.gutenberg.UserGutenbergDataRepositoryImpl
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

    @Binds
    internal abstract fun bindsGutenbergRecentSearchRepository(
        gutenbergRecentSearchRepository: GutenbergRecentSearchRepositoryImpl
    ): GutenbergRecentSearchRepository

}