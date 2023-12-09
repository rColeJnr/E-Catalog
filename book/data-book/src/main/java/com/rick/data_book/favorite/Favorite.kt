package com.rick.data_book.favorite

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "favorite")
data class Favorite(
    @PrimaryKey(autoGenerate = true) @SerializedName("id") val id: Long? = null,
    @SerializedName("title")
    val title: String,
    @SerializedName("authors")
    val author: String,
    @SerializedName("image")
    val image: String
): Parcelable