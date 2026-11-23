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
