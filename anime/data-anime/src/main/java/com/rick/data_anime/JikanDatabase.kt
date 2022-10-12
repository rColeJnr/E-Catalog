package com.rick.data_anime

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rick.data_anime.model_anime.Anime
import com.rick.data_anime.model_manga.Manga

@Database(
    entities = [
        Manga::class,
        Anime::class,
        MangaRemoteKeys::class,
        AnimeRemoteKeys::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(MangaConverters::class, AnimeConverters::class)
abstract class JikanDatabase : RoomDatabase() {

    abstract val mangaDao: MangaDao
    abstract val mangaRemoteKeysDao: MangaRemoteKeysDao
    abstract val animeDao: AnimeDao
    abstract val animeRemoteKeysDao: AnimeRemoteKeysDao

    companion object {
        const val JIKAN_DATABASE_NAME = "JIKAN_DB"
    }

}