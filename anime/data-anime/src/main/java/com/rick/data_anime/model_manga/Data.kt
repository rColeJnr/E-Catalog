package com.rick.data_anime.model_manga


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Data(
    @SerializedName("mal_id")
    val malId: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("images")
    val images: Images,
    @SerializedName("approved")
    val approved: Boolean,
    @SerializedName("titles")
    val titles: List<Title>,
    @SerializedName("title")
    val title: String,
    @SerializedName("title_english")
    val titleEnglish: String,
    @SerializedName("title_japanese")
    val titleJapanese: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("chapters")
    val chapters: Int,
    @SerializedName("volumes")
    val volumes: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("publishing")
    val publishing: Boolean,
    @SerializedName("published")
    val published: Published,
    @SerializedName("score")
    val score: Int,
    @SerializedName("scored_by")
    val scoredBy: Int,
    @SerializedName("rank")
    val rank: Int,
    @SerializedName("popularity")
    val popularity: Int,
    @SerializedName("members")
    val members: Int,
    @SerializedName("favorites")
    val favorites: Int,
    @SerializedName("synopsis")
    val synopsis: String,
    @SerializedName("background")
    val background: String,
    @SerializedName("authors")
    val authors: List<Author>,
    @SerializedName("serializations")
    val serializations: List<Serialization>,
    @SerializedName("genres")
    val genres: List<Genre>,
    @SerializedName("explicit_genres")
    val explicitGenres: List<ExplicitGenre>,
    @SerializedName("themes")
    val themes: List<Theme>,
    @SerializedName("demographics")
    val demographics: List<Demographic>
) : Parcelable