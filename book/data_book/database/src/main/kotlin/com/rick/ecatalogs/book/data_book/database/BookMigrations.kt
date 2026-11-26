package com.rick.ecatalogs.book.data_book.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * 1 -> 2: bestsellers are stored per genre (new `genre` column, primary key (id, genre)) and keyed
 * by ISBN instead of rank. The table is a network cache that's refetched when a list is opened,
 * so it's recreated rather than copied (old rows were keyed by rank and can't be converted).
 */
internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `bestseller_table`")
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `bestseller_table` (`id` TEXT NOT NULL, " +
                "`rank` INTEGER NOT NULL, `rankLastWeek` INTEGER NOT NULL, " +
                "`weeksOnList` INTEGER NOT NULL, `publisher` TEXT NOT NULL, " +
                "`description` TEXT NOT NULL, `title` TEXT NOT NULL, `author` TEXT NOT NULL, " +
                "`image` TEXT NOT NULL, `amazonLink` TEXT NOT NULL, `buyLinks` TEXT NOT NULL, " +
                "`genre` TEXT NOT NULL, PRIMARY KEY(`id`, `genre`))"
        )
    }
}

/**
 * 2 -> 3: `catalog_position` marks the rows of the catalog list and keeps the API's order,
 * so search results and kept favorites no longer show up in the list. The rows already stored stay in the list (in their old order) until its next refresh, so no screen opens empty.
 */
internal val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `gutenberg_table` ADD COLUMN `catalog_position` INTEGER")
        db.execSQL("UPDATE `gutenberg_table` SET `catalog_position` = -`downloads`")
    }
}
