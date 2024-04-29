package com.rick.data.database_anime.di

import com.rick.data.database_anime.JikanDatabase
import com.rick.data.database_anime.dao.AnimeDao
import com.rick.data.database_anime.dao.AnimeRecentSearchQueryDao
import com.rick.data.database_anime.dao.AnimeRemoteKeysDao
import com.rick.data.database_anime.dao.MangaDao
import com.rick.data.database_anime.dao.MangaRecentSearchQueryDao
import com.rick.data.database_anime.dao.MangaRemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {

    @Provides
    fun providesMangaDao(jikanDatabase: JikanDatabase): MangaDao =
        jikanDatabase.mangaDao

    @Provides
    fun providesAnimeDao(jikanDatabase: JikanDatabase): AnimeDao =
        jikanDatabase.animeDao

    @Provides
    fun providesAnimeRemoteKeysDao(jikanDatabase: JikanDatabase): AnimeRemoteKeysDao =
        jikanDatabase.animeRemoteKeysDao

    @Provides
    fun providesMangaRemoteKeysDao(jikanDatabase: JikanDatabase): MangaRemoteKeysDao =
        jikanDatabase.mangaRemoteKeysDao

    @Provides
    fun provideAnimeRecentSearchQueryDao(jikanDatabase: JikanDatabase): AnimeRecentSearchQueryDao =
        jikanDatabase.animeRecentSearchQueryDao

    @Provides
    fun provideMangaRecentSearchQueryDao(jikanDatabase: JikanDatabase): MangaRecentSearchQueryDao =
        jikanDatabase.mangaRecentSearchQueryDao
}