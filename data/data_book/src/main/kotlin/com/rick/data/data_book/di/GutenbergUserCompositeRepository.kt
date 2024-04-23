package com.rick.data.data_book.di

import com.rick.data.data_book.repository.gutenberg.CompositeGutenbergRepository
import com.rick.data.data_book.repository.gutenberg.UserGutenbergRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface GutenbergUserCompositeRepository {

    @Binds
    fun bindsUserGutenbergRepository(
        compositeRepository: CompositeGutenbergRepository
    ): UserGutenbergRepository

}