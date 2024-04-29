package com.rick.anime.data_anime.data.di

import com.rick.anime.data_anime.data.repository.anime.CompositeAnimeRepository
import com.rick.anime.data_anime.data.repository.anime.UserAnimeRepository
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