package com.rick.anime.data_anime.data.di

import com.rick.anime.data_anime.data.repository.manga.CompositeMangaRepository
import com.rick.anime.data_anime.data.repository.manga.UserMangaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
internal interface MangaUserCompositeRepository {

    @Binds
    fun bindsUserMangaRepository(
        compositeRepository: CompositeMangaRepository
    ): UserMangaRepository

}