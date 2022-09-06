package com.rick.data_movie.imdb.movie_model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class IMDBMovie(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("originalTitle")
    val originalTitle: String,
    @SerializedName("fullTitle")
    val fullTitle: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("year")
    val year: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("releaseDate")
    val releaseDate: String,
    @SerializedName("runtimeMins")
    val runtimeMins: String?,
    @SerializedName("runtimeStr")
    val runtimeStr: String?,
    @SerializedName("plot")
    val plot: String,
    @SerializedName("plotLocal")
    val plotLocal: String,
    @SerializedName("plotLocalIsRtl")
    val plotLocalIsRtl: Boolean,
    @SerializedName("awards")
    val awards: String,
    @SerializedName("directors")
    val directors: String,
    @SerializedName("directorList")
    val directorList: List<Director>,
    @SerializedName("writers")
    val writers: String,
    @SerializedName("writerList")
    val writerList: List<Writer>,
    @SerializedName("stars")
    val stars: String,
    @SerializedName("starList")
    val starList: List<Star>,
    @SerializedName("actorList")
    val actorList: List<Actor>,
    @SerializedName("genres")
    val genres: String,
    @SerializedName("genreList")
    val genreList: List<Genre>,
    @SerializedName("companies")
    val companies: String,
    @SerializedName("companyList")
    val companyList: List<Company>,
    @SerializedName("countries")
    val countries: String,
    @SerializedName("countryList")
    val countryList: List<Country>,
    @SerializedName("languages")
    val languages: String,
    @SerializedName("languageList")
    val languageList: List<Language>,
    @SerializedName("contentRating")
    val contentRating: String,
    @SerializedName("imDbRating")
    val imDbRating: String,
    @SerializedName("imDbRatingVotes")
    val imDbRatingVotes: String,
    @SerializedName("metacriticRating")
    val metacriticRating: String,
    @SerializedName("ratings")
    val ratings: Ratings,
//    @SerializedName("wikipedia")
//    val wikipedia: Any?,
    @SerializedName("posters")
    val posters: Posters,
    @SerializedName("images")
    val images: com.rick.data_movie.imdb.movie_model.Image,
//    @SerializedName("trailer")
//    val trailer: Any?,
    @SerializedName("boxOffice")
    val boxOffice: BoxOffice,
    @SerializedName("keywords")
    val keywords: String,
    @SerializedName("keywordList")
    val keywordList: List<String>,
    @SerializedName("similars")
    val similars: List<Similar>,
    @SerializedName("tvSeriesInfo")
    val tvSeriesInfo: TvSeriesInfo?,
//    @SerializedName("tvEpisodeInfo")
//    val tvEpisodeInfo: Any?,
    @SerializedName("errorMessage")
    val errorMessage: String?
) : Parcelable

