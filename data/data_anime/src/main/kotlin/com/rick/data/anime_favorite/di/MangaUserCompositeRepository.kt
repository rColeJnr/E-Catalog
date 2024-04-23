package com.rick.data.anime_favorite.di

import com.rick.data.anime_favorite.repository.manga.CompositeMangaRepository
import com.rick.data.anime_favorite.repository.manga.UserMangaRepository
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