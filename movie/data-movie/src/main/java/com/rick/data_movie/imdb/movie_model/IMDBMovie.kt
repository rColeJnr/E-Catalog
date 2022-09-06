package com.rick.data_movie.imdb.movie_model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class IMDBMovieDto(
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
    val directorList: List<DirectorDto>,
    @SerializedName("writers")
    val writers: String,
    @SerializedName("writerList")
    val writerList: List<WriterDto>,
    @SerializedName("stars")
    val stars: String,
    @SerializedName("starList")
    val starList: List<StarDto>,
    @SerializedName("actorList")
    val actorList: List<ActorDto>,
    @SerializedName("genres")
    val genres: String,
    @SerializedName("genreList")
    val genreList: List<GenreDto>,
    @SerializedName("companies")
    val companies: String,
    @SerializedName("companyList")
    val companyList: List<CompanyDto>,
    @SerializedName("countries")
    val countries: String,
    @SerializedName("countryList")
    val countryList: List<CountryDto>,
    @SerializedName("languages")
    val languages: String,
    @SerializedName("languageList")
    val languageList: List<LanguageDto>,
    @SerializedName("contentRating")
    val contentRating: String,
    @SerializedName("imDbRating")
    val imDbRating: String,
    @SerializedName("imDbRatingVotes")
    val imDbRatingVotes: String,
    @SerializedName("metacriticRating")
    val metacriticRating: String,
    @SerializedName("ratings")
    val ratings: RatingsDto,
    @SerializedName("wikipedia")
    val wikipedia: Any?,
    @SerializedName("posters")
    val posters: PostersDto,
    @SerializedName("images")
    val images: ImageDto,
    @SerializedName("trailer")
    val trailer: Any?,
    @SerializedName("boxOffice")
    val boxOffice: BoxOfficeDto,
    @SerializedName("keywords")
    val keywords: String,
    @SerializedName("keywordList")
    val keywordList: List<String>,
    @SerializedName("similars")
    val similars: List<SimilarDto>,
    @SerializedName("tvSeriesInfo")
    val tvSeriesInfo: TvSeriesInfoDto?,
    @SerializedName("tvEpisodeInfo")
    val tvEpisodeInfo: Any?,
    @SerializedName("errorMessage")
    val errorMessage: String?
) {
    fun toImdbMovie(): IMDBMovie =
        IMDBMovie(
            id,
            title,
            type,
            year,
            image,
            releaseDate,
            runtimeStr,
            plot,
            awards,
            directors,
            writers,
            actorList.toActorList(),
            genres,
            companies,
            countries,
            languages,
            ratings.toRatings(),
            images.toImages(),
            boxOffice.toBoxOffice(),
            similars.toSimilars(),
            tvSeriesInfo.toTvSeriesInfo(),
            errorMessage
        )
}


@Parcelize
@Entity(tableName = "imdb_movie")
data class IMDBMovie(
    @SerializedName("id")
    @PrimaryKey(autoGenerate = false) val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("year")
    val year: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("releaseDate")
    val releaseDate: String,
    @SerializedName("runtimeStr")
    val runtimeStr: String?,
    @SerializedName("plot")
    val plot: String,
    @SerializedName("awards")
    val awards: String,
    @SerializedName("directors")
    val directors: String,
    @SerializedName("writers")
    val writers: String,
    @SerializedName("actorList")
    val actorList: List<Actor>,
    @SerializedName("genres")
    val genres: String,
    @SerializedName("companies")
    val companies: String,
    @SerializedName("countries")
    val countries: String,
    @SerializedName("languages")
    val languages: String,
    @SerializedName("ratings")
    val ratings: Ratings,
    @SerializedName("images")
    val images: Image,
    @SerializedName("boxOffice")
    val boxOffice: BoxOffice,
    @SerializedName("similars")
    val similars: List<Similar>,
    @SerializedName("tvSeriesInfo")
    val tvSeriesInfo: TvSeriesInfo?,
    @SerializedName("errorMessage")
    val errorMessage: String?
) : Parcelable
