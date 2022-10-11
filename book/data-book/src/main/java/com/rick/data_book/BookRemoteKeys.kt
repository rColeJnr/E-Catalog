package com.rick.data_book

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_remote_keys")
data class BookRemoteKeys(
    @PrimaryKey val book: Int,
    val prevKey: Int?,
    val nextKey: Int?
)
