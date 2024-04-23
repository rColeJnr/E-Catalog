package com.rick.data.anime_favorite.di

import com.rick.data.anime_favorite.repository.manga.MangaRepository
import com.rick.data.anime_favorite.repository.manga.MangaRepositoryImpl
import com.rick.data.anime_favorite.repository.manga.UserMangaDataRepository
import com.rick.data.anime_favorite.repository.manga.UserMangaDataRepositoryImpl
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

}