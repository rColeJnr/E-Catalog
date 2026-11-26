package com.rick.ecatalogs.movie.data_movie.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * 1 -> 2: `catalog_position` marks the rows of the catalog list and keeps the API's order,
 * so search results and kept favorites no longer show up in the list. The rows already stored stay in the list (in their old order) until its next refresh, so no screen opens empty.
 */
internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `trending_movie_table` ADD COLUMN `catalog_position` INTEGER")
        db.execSQL("UPDATE `trending_movie_table` SET `catalog_position` = rowid")
        db.execSQL("ALTER TABLE `trending_series_table` ADD COLUMN `catalog_position` INTEGER")
        db.execSQL("UPDATE `trending_series_table` SET `catalog_position` = rowid")
        db.execSQL("ALTER TABLE `article_table` ADD COLUMN `catalog_position` INTEGER")
        db.execSQL("UPDATE `article_table` SET `catalog_position` = rowid")
    }
}
