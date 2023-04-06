package com.rick.data_book.favorite

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "favorite")
data class Favorite(
    val title: String,
    val author: String
): Parcelable