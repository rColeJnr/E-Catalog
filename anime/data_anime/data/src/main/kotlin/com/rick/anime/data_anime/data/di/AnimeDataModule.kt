package com.rick.anime.data_anime.data.di

import com.rick.anime.data_anime.data.repository.anime.AnimeByIdRepository
import com.rick.anime.data_anime.data.repository.anime.AnimeByIdRepositoryImpl
import com.rick.anime.data_anime.data.repository.anime.AnimeRecentSearchRepository
import com.rick.anime.data_anime.data.repository.anime.AnimeRecentSearchRepositoryImpl
import com.rick.anime.data_anime.data.repository.anime.AnimeRepository
import com.rick.anime.data_anime.data.repository.anime.AnimeRepositoryImpl
import com.rick.anime.data_anime.data.repository.anime.UserAnimeDataRepository
import com.rick.anime.data_anime.data.repository.anime.UserAnimeDataRepositoryImpl
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

    @Binds
    internal abstract fun bindsAnimeRecentSearchRepository(
        animeRecentSearchRepository: AnimeRecentSearchRepositoryImpl
    ): AnimeRecentSearchRepository

    @Binds
    internal abstract fun bindsAnimeByIdRepository(
        animeByIdRepositoryImpl: AnimeByIdRepositoryImpl
    ): AnimeByIdRepository

}