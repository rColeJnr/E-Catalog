package com.rick.ecatalogs.anime.data_anime.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rick.ecatalogs.anime.data_anime.database.dao.AnimeDao
import com.rick.ecatalogs.anime.data_anime.database.dao.AnimeRecentSearchQueryDao
import com.rick.ecatalogs.anime.data_anime.database.dao.AnimeRemoteKeysDao
import com.rick.ecatalogs.anime.data_anime.database.dao.MangaDao
import com.rick.ecatalogs.anime.data_anime.database.dao.MangaRecentSearchQueryDao
import com.rick.ecatalogs.anime.data_anime.database.dao.MangaRemoteKeysDao
import com.rick.ecatalogs.anime.data_anime.database.model.AnimeEntity
import com.rick.ecatalogs.anime.data_anime.database.model.AnimeRecentSearchQueryEntity
import com.rick.ecatalogs.anime.data_anime.database.model.AnimeRemoteKeys
import com.rick.ecatalogs.anime.data_anime.database.model.MangaEntity
import com.rick.ecatalogs.anime.data_anime.database.model.MangaRecentSearchQueryEntity
import com.rick.ecatalogs.anime.data_anime.database.model.MangaRemoteKeys
import com.rick.ecatalogs.anime.data_anime.database.util.JikanConverters

@Database(
    entities = [
        MangaEntity::class,
        AnimeEntity::class,
        MangaRemoteKeys::class,
        AnimeRemoteKeys::class,
        AnimeRecentSearchQueryEntity::class,
        MangaRecentSearchQueryEntity::class
    ],
    version = 2,
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