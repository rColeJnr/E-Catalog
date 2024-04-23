package com.rick.data.anime_favorite.di

import com.rick.data.anime_favorite.repository.anime.CompositeAnimeRepository
import com.rick.data.anime_favorite.repository.anime.UserAnimeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface AnimeUserCompositeRepository {

    @Binds
    fun bindsUserAnimeRepository(
        compositeRepository: CompositeAnimeRepository
    ): UserAnimeRepository

}