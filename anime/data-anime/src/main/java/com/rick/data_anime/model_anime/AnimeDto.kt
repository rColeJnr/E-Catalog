package com.rick.data_anime.model_anime


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class AnimeDto(
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
    val title: String,
    @SerializedName("title_english")
    val titleEnglish: String?,
    @SerializedName("title_japanese")
    val titleJapanese: String,
    @SerializedName("title_synonyms")
    val titleSynonyms: List<String>,
    @SerializedName("type")
    val type: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("episodes")
    val episodes: Int?,
    @SerializedName("status")
    val status: String,
    @SerializedName("airing")
    val airing: Boolean,
    @SerializedName("aired")
    val aired: AiredDto,
    @SerializedName("duration")
    val duration: String,
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
    val synopsis: String,
    @SerializedName("background")
    val background: String?,
    @SerializedName("season")
    val season: String?,
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
    val explicitGenres: List<Any>,
    @SerializedName("themes")
    val themes: List<Theme>,
    @SerializedName("demographics")
    val demographics: List<Demographic>
)

@Parcelize
@Entity(tableName = "anime_db")
data class Anime(
    @PrimaryKey (autoGenerate = false)
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
    @SerializedName("title_japanese")
    val titleJapanese: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("episodes")
    val episodes: Int?,
    @SerializedName("status")
    val airingStatus: String,
    @SerializedName("aired")
    val aired: Aired,
    @SerializedName("duration")
    val duration: String,
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
    val synopsis: String,
    @SerializedName("background")
    val background: String?,
    @SerializedName("genres")
    val genres: List<Genre>,
) : Parcelable