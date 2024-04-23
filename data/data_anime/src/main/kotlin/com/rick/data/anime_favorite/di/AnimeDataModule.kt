package com.rick.data.anime_favorite.di

import com.rick.data.anime_favorite.repository.AnimeRepository
import com.rick.data.anime_favorite.repository.AnimeRepositoryImpl
import com.rick.data.anime_favorite.repository.UserAnimeDataRepository
import com.rick.data.anime_favorite.repository.UserAnimeDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AnimeDataModule {

    @Binds
    internal abstract fun bindsAnimeUserDataRepository(
        userDataRepository: UserAnimeDataRepositoryImpl
    ): UserAnimeDataRepository

    @Binds
    internal abstract fun binsAnimeRepository(
        userAnimeRepositoryImpl: AnimeRepositoryImpl
    ): AnimeRepository

}