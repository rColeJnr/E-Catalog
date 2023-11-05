package com.rick.data_movie.ny_times_deprecated

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Deprecated("Use Movie from movie/data-movie/src/main/java/com/rick/data_movie/ny_times/Movie.kt")
@Parcelize
@Entity(tableName = "movies_db")
data class Movie(
    @SerializedName("id")
    @PrimaryKey (autoGenerate = false) var id: Long? = null,
    @SerializedName("title")
    val title: String,
    @SerializedName("summar")
    val summary: String,
    @SerializedName("rating")
    val rating: String,
    @SerializedName("opening_data")
    val openingDate: String?,
    @SerializedName("link")
    val link: Link,
    @SerializedName("multimedia")
    val multimedia: Multimedia,
    @SerializedName("favorite")
    val favorite: Boolean,
): Parcelable {
//    fun toFavorite(): Favorite =
//        Favorite(
//            id = id,
//            title = title,
//            overview = summary,
//            image = multimedia.src,
//            type = "Movie"
//        )
}
