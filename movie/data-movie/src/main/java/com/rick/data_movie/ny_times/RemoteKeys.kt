package com.rick.data_movie.ny_times

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey (autoGenerate = false) val id: Int,
    val prevKey: Int?,
    val nextKey: Int?
)
