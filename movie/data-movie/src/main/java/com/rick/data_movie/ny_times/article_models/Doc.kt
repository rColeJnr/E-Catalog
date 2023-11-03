package com.rick.data_movie.ny_times.article_models


import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ny_times_article")
data class Doc(
    @SerializedName("_id")
    val id: String,
    @SerializedName("web_url")
    val webUrl: String,
    @SerializedName("snippet")
    val snippet: String,
    @SerializedName("lead_paragraph")
    val leadParagraph: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("multimedia")
    val multimedia: List<Multimedia>,
    @SerializedName("headline")
    val headline: Headline,
    @SerializedName("keywords")
    val keywords: List<Keyword>,
    @SerializedName("pub_date")
    val pubDate: String,
    @SerializedName("section_name")
    val sectionName: String,
    @SerializedName("byline")
    val byline: Byline,
    @SerializedName("favorite")
    val favorite: Boolean,
): Parcelable