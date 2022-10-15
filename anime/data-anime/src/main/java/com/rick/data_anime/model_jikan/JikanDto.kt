package com.rick.data_anime.model_jikan


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.rick.data_anime.model_manga.Published
import com.rick.data_anime.model_manga.PublishedDto
import kotlinx.parcelize.Parcelize


data class JikanDto(
    @SerializedName("mal_id")
    val malId: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("images")
    val images: ImagesDto,
    @SerializedName("trailer")
    val trailer: TrailerDto,
    @SerializedName("approved")
    val approved: Boolean,
    @SerializedName("titles")
    val titles: List<Title>,
    @SerializedName("title")
    val title: String?,
    @SerializedName("title_english")
    val titleEnglish: String?,
    @SerializedName("title_japanese")
    val titleJapanese: String?,
    @SerializedName("title_synonyms")
    val titleSynonyms: List<String>,
    @SerializedName("type")
    val type: String,
    @SerializedName("chapters")
    val chapters: Int?,
    @SerializedName("volumes")
    val volumes: Int?,
    @SerializedName("source")
    val source: String,
    @SerializedName("episodes")
    val episodes: Int?,
    @SerializedName("status")
    val status: String,
    @SerializedName("airing")
    val airing: Boolean,
    @SerializedName("publishing")
    val publishing: Boolean,
    @SerializedName("published")
    val publishedDto: PublishedDto,
    @SerializedName("aired")
    val aired: AiredDto,
    @SerializedName("duration")
    val duration: String?,
    @SerializedName("rating")
    val rating: String,
    @SerializedName("score")
    val score: Double,
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
    val synopsis: String?,
    @SerializedName("background")
    val background: String?,
    @SerializedName("season")
    val season: String?,
    @SerializedName("authors")
    val authors: List<Author>,
    @SerializedName("serializations")
    val serializations: List<Serialization>,
    @SerializedName("year")
    val year: Int?,
    @SerializedName("broadcast")
    val broadcast: Broadcast,
    @SerializedName("producers")
    val producers: List<Producer>,
    @SerializedName("licensors")
    val licensors: List<Licensor>,
    @SerializedName("studios")
    val studios: List<Studio>,
    @SerializedName("genres")
    val genres: List<GenreDto>,
    @SerializedName("explicit_genres")
    val explicitGenres: List<ExplicitGenre>,
    @SerializedName("themes")
    val themes: List<Theme>,
    @SerializedName("demographics")
    val demographics: List<Demographic>
)

@Parcelize
@Entity(tableName = "jikan_db")
data class Jikan(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("mal_id")
    val malId: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("images")
    val images: Images,
    @SerializedName("trailer")
    val trailer: Trailer,
    @SerializedName("title")
    val title: String?,
    @SerializedName("type")
    val type: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("episodes")
    val episodes: Int?,
    @SerializedName("chapters")
    val chapters: Int?,
    @SerializedName("volumes")
    val volumes: Int?,
    @SerializedName("status")
    val status: String,
    @SerializedName("aired")
    val aired: Aired,
    @SerializedName("published")
    val published: Published,
    @SerializedName("duration")
    val duration: String?,
    @SerializedName("rating")
    val rating: String,
    @SerializedName("score")
    val score: Double,
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
    val synopsis: String?,
    @SerializedName("background")
    val background: String?,
    @SerializedName("genres")
    val genres: List<Genre>,
    @SerializedName("authors")
    val authors: List<Author>,
    @SerializedName("serializations")
    val serializations: List<Serialization>,
    @SerializedName("themes")
    val themes: List<Theme>
) : Parcelable