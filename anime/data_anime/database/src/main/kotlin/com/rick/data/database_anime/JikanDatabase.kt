package com.rick.data.database_anime

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rick.data.database_anime.dao.AnimeDao
import com.rick.data.database_anime.dao.AnimeRecentSearchQueryDao
import com.rick.data.database_anime.dao.AnimeRemoteKeysDao
import com.rick.data.database_anime.dao.MangaDao
import com.rick.data.database_anime.dao.MangaRecentSearchQueryDao
import com.rick.data.database_anime.dao.MangaRemoteKeysDao
import com.rick.data.database_anime.model.AnimeEntity
import com.rick.data.database_anime.model.AnimeRecentSearchQueryEntity
import com.rick.data.database_anime.model.AnimeRemoteKeys
import com.rick.data.database_anime.model.MangaEntity
import com.rick.data.database_anime.model.MangaRecentSearchQueryEntity
import com.rick.data.database_anime.model.MangaRemoteKeys
import com.rick.data.database_anime.util.JikanConverters

@Database(
    entities = [
        MangaEntity::class,
        AnimeEntity::class,
        MangaRemoteKeys::class,
        AnimeRemoteKeys::class,
        AnimeRecentSearchQueryEntity::class,
        MangaRecentSearchQueryEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(JikanConverters::class)
abstract class JikanDatabase : RoomDatabase() {

    abstract val mangaRemoteKeysDao: MangaRemoteKeysDao
    abstract val mangaDao: MangaDao
    abstract val animeDao: AnimeDao
    abstract val animeRemoteKeysDao: AnimeRemoteKeysDao
    abstract val animeRecentSearchQueryDao: AnimeRecentSearchQueryDao
    abstract val mangaRecentSearchQueryDao: MangaRecentSearchQueryDao


}