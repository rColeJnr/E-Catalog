package com.rick.data_anime

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rick.data_anime.model_jikan.Jikan
import com.rick.data_anime.model_manga.Manga

@Database(
    entities = [
        Jikan::class,
        MangaRemoteKeys::class,
        AnimeRemoteKeys::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(JikanConverters::class)
abstract class JikanDatabase : RoomDatabase() {

    abstract val mangaRemoteKeysDao: MangaRemoteKeysDao
    abstract val animeDao: JikanDao
    abstract val animeRemoteKeysDao: AnimeRemoteKeysDao

    companion object {
        const val JIKAN_DATABASE_NAME = "JIKAN_DB"
    }

}