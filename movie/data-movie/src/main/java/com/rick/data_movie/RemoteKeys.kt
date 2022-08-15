package com.rick.data_movie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey (autoGenerate = false) val movie: String,
    val prevKey: Int?,
    val nextKey: Int?
)