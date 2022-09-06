package com.rick.data_movie

import android.provider.MediaStore
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.rick.core.JsonParser
import com.rick.data_movie.imdb.movie_model.*

@ProvidedTypeConverter
class IMDBConverters(
    private val jsonParser: JsonParser
) {

    @TypeConverter
    fun fromActorListToString(actorList: List<Actor>): String =
        jsonParser.toJson(
            actorList,
            object : TypeToken<List<Actor>>() {}.type
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
    fun fromImagesToString(images: MediaStore.Images): String =
        jsonParser.toJson(
            images,
            object : TypeToken<MediaStore.Images>() {}.type
        ) ?: "[]"

    fun fromStringToImages(images: String): MediaStore.Images =
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