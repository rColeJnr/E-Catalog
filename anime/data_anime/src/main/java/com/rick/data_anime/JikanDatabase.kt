package com.rick.data_anime

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rick.data_anime.favorite.FavDao
import com.rick.data_anime.favorite.JikanFavorite
import com.rick.data_anime.model_jikan.Jikan
@Database(
    entities = [
        Jikan::class,
        JikanFavorite::class,
        MangaRemoteKeys::class,
        AnimeRemoteKeys::class,
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(JikanConverters::class)
abstract class JikanDatabase : RoomDatabase() {

    abstract val mangaRemoteKeysDao: MangaRemoteKeysDao
    abstract val jikanDao: JikanDao
    abstract val favDao: FavDao
    abstract val animeRemoteKeysDao: AnimeRemoteKeysDao

    companion object {
        const val JIKAN_DATABASE_NAME = "JIKAN_DB"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE jikan_db ADD COLUMN favorite BOOLEAN")
            }
        }
    }

}