package com.rick.data_movie.ny_times

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "movies_db")
data class Movie(
    @PrimaryKey (autoGenerate = true) val id: Long? = null,
    val title: String,
    val summary: String,
    val rating: String,
    val openingDate: String?,
    val link: Link,
    val multimedia: Multimedia,
): Parcelable
