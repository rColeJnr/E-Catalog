package com.rick.data_anime.model_manga


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class MangaDto(
    @SerializedName("mal_id")
    val malId: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("images")
    val images: ImagesDto,
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
    val publishedDto: PublishedDto,
    @SerializedName("score")
    val score: Float,
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
    val genres: List<GenreDto>,
    @SerializedName("explicit_genres")
    val explicitGenres: List<ExplicitGenre>,
    @SerializedName("themes")
    val themes: List<ThemeDto>,
    @SerializedName("demographics")
    val demographics: List<Demographic>
)

@Entity(tableName = "manga_db")
@Parcelize
data class Manga(
    @PrimaryKey (autoGenerate = false)
    @SerializedName("mal_id")
    val malId: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("images")
    val imagesDto: Images,
    @SerializedName("approved")
    val approved: Boolean,
    @SerializedName("title")
    val title: String,
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
    @SerializedName("published")
    val published: Published,
    @SerializedName("score")
    val score: Float,
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
    @SerializedName("themes")
    val themes: List<Theme>
) : Parcelable