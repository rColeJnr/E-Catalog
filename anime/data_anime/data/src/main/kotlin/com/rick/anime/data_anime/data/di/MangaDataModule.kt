package com.rick.anime.data_anime.data.di

import com.rick.anime.data_anime.data.repository.manga.MangaByIdRepository
import com.rick.anime.data_anime.data.repository.manga.MangaByIdRepositoryImpl
import com.rick.anime.data_anime.data.repository.manga.MangaRecentSearchRepository
import com.rick.anime.data_anime.data.repository.manga.MangaRecentSearchRepositoryImpl
import com.rick.anime.data_anime.data.repository.manga.MangaRepository
import com.rick.anime.data_anime.data.repository.manga.MangaRepositoryImpl
import com.rick.anime.data_anime.data.repository.manga.UserMangaDataRepository
import com.rick.anime.data_anime.data.repository.manga.UserMangaDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MangaDataModule {

    @Binds
    internal abstract fun bindsMangaUserDataRepository(
        userDataRepository: UserMangaDataRepositoryImpl
    ): UserMangaDataRepository

    @Binds
    internal abstract fun bindsMangaRepository(
        mangaRepository: MangaRepositoryImpl
    ): MangaRepository

    @Binds
    internal abstract fun bindsMangaRecentSearchRepository(
        mangaRecentSearchRepository: MangaRecentSearchRepositoryImpl
    ): MangaRecentSearchRepository

    @Binds
    internal abstract fun bindsMangaByIdRepository(
        mangaByIdRepositoryImpl: MangaByIdRepositoryImpl
    ): MangaByIdRepository
}