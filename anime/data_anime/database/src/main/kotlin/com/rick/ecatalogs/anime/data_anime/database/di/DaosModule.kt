package com.rick.ecatalogs.anime.data_anime.database.di

import com.rick.ecatalogs.anime.data_anime.database.JikanDatabase
import com.rick.ecatalogs.anime.data_anime.database.dao.AnimeDao
import com.rick.ecatalogs.anime.data_anime.database.dao.AnimeRecentSearchQueryDao
import com.rick.ecatalogs.anime.data_anime.database.dao.AnimeRemoteKeysDao
import com.rick.ecatalogs.anime.data_anime.database.dao.MangaDao
import com.rick.ecatalogs.anime.data_anime.database.dao.MangaRecentSearchQueryDao
import com.rick.ecatalogs.anime.data_anime.database.dao.MangaRemoteKeysDao
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