package com.rick.data_movie

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.rick.core.JsonParser
import com.rick.data_movie.imdb.movie_model.*
import com.rick.data_movie.ny_times.Link
import com.rick.data_movie.ny_times.Movie
import com.rick.data_movie.ny_times.Multimedia

@ProvidedTypeConverter
class Converters(
    private val jsonParser: JsonParser
) {

    // convert json back to List<Result>
    @TypeConverter
    fun fromFeatureJson(json: String): List<Movie> =
        jsonParser.fromJson<ArrayList<Movie>>(
            json,
            object : TypeToken<ArrayList<Movie>>() {}.type
        ) ?: emptyList()

    // convert List<Result> to json
    @TypeConverter
    fun toFeatureJson(features: List<Movie>): String =
        jsonParser.toJson(
            features,
            object : TypeToken<ArrayList<Movie>>() {}.type
        ) ?: "[]"

    @TypeConverter
    fun fromLinktoString(link: Link): String =
        link.url

    @TypeConverter
    fun fromStringToLink(string: String): Link =
        Link(string)

    @TypeConverter
    fun fromMultimediatoString(multimedia: Multimedia): String =
        multimedia.src

    @TypeConverter
    fun fromStringToMultimedia(string: String): Multimedia =
        Multimedia(string)

    @TypeConverter
    fun fromActorListToString(actorList: List<Actor>): String =
        jsonParser.toJson(
            actorList,
            object : TypeToken<ArrayList<Actor>>() {}.type
        ) ?: "[]"

    fun fromStringToActorList(actorList: String): List<Actor> =
        jsonParser.fromJson<ArrayList<Actor>>(actorList,
            object : TypeToken<ArrayList<Actor>>() {}.type
        ) ?: emptyList()

    @TypeConverter
    fun fromDirectorListToString(directorList: List<Director>): String =
        jsonParser.toJson(
            directorList,
            object : TypeToken<ArrayList<Director>>() {}.type
        ) ?: "[]"

    fun fromStringToDirectorList(directorList: String): List<Director> =
        jsonParser.fromJson<ArrayList<Director>>(directorList,
            object : TypeToken<ArrayList<Director>>() {}.type
        ) ?: emptyList()

    @TypeConverter
    fun fromRatingsToString(ratings: Ratings): String =
        jsonParser.toJson(
            ratings,
            object : TypeToken<Ratings>() {}.type
        ) ?: "[]"

    fun fromStringToRatings(ratings: String): Ratings =
        jsonParser.fromJson<Ratings>(ratings,
            object : TypeToken<Ratings>() {}.type
        )!!

    @TypeConverter
    fun fromImagesToString(images: Images): String =
        jsonParser.toJson(
            images,
            object : TypeToken<Images>() {}.type
        ) ?: "[]"

    fun fromStringToImages(images: String): Images =
        jsonParser.fromJson<Images>(images,
            object : TypeToken<Images>() {}.type
        )!!

    @TypeConverter
    fun fromOfficeToString(boxOffice: BoxOffice): String =
        jsonParser.toJson(
            boxOffice,
            object : TypeToken<BoxOffice>() {}.type
        ) ?: "[]"

    fun fromStringToOffice(boxOffice: String):BoxOffice =
        jsonParser.fromJson<BoxOffice>(boxOffice,
            object : TypeToken<BoxOffice>() {}.type
        )!!

    @TypeConverter
    fun fromSimilarToString(similars: List<Similar>): String =
        jsonParser.toJson(
            similars,
            object : TypeToken<ArrayList<Similar>>() {}.type
        ) ?: "[]"

    fun fromStringToSimilars(similars: String): List<Similar> =
        jsonParser.fromJson<ArrayList<Similar>>(similars,
            object : TypeToken<ArrayList<Similar>>() {}.type
        ) ?: emptyList()
}