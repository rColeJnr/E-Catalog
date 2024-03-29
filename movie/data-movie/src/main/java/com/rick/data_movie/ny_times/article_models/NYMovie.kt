package com.rick.data_movie.ny_times.article_models


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.rick.data_movie.favorite.Favorite
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ny_times_article")
data class NYMovie(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @SerializedName("web_url")
    val webUrl: String,
    @SerializedName("snippet")
    val summary: String,
    @SerializedName("lead_paragraph")
    val leadParagraph: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("multimedia")
    val multimedia: List<Multimedia>,
    @SerializedName("headline")
    val headline: Headline,
    @SerializedName("pub_date")
    val pubDate: String,
    @SerializedName("byline")
    val byline: Byline,
    @SerializedName("favorite")
    val favorite: Boolean
): Parcelable {
    fun toFavorite(): Favorite =
        Favorite (
            id = id,
            title = headline.main,
            summary = summary,
            image = multimedia[0].url,
            type = "tv"
        )
}