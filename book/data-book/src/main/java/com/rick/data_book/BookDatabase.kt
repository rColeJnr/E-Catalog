package com.rick.data_book

import androidx.room.RoomDatabase

abstract class BookDatabase: RoomDatabase() {

    val bookDao: BookDao

}