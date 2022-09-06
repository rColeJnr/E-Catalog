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
    fun fromActorListToString(actorList: List<ActorDto>): String =
        jsonParser.toJson(
            actorList,
            object : TypeToken<List<ActorDto>>() {}.type
        ) ?: "[]"

    @TypeConverter
    fun fromStringToActorList(actorList: String): List<ActorDto> =
        jsonParser.fromJson<ArrayList<ActorDto>>(actorList,
            object : TypeToken<ArrayList<ActorDto>>() {}.type
        ) ?: emptyList()

    @TypeConverter
    fun fromDirectorListToString(directorList: List<Director>): String =
        jsonParser.toJson(
            directorList,
            object : TypeToken<ArrayList<Director>>() {}.type
        ) ?: "[]"

    @TypeConverter
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

    @TypeConverter
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

    @TypeConverter
    fun fromStringToImages(images: String): MediaStore.Images =
        jsonParser.fromJson<MediaStore.Images>(images,
            object : TypeToken<MediaStore.Images>() {}.type
        )!!

    @TypeConverter
    fun fromOfficeToString(boxOffice: BoxOffice): String =
        jsonParser.toJson(
            boxOffice,
            object : TypeToken<BoxOffice>() {}.type
        ) ?: "[]"

    @TypeConverter
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

    @TypeConverter
    fun fromStringToSimilars(similars: String): List<Similar> =
        jsonParser.fromJson<ArrayList<Similar>>(similars,
            object : TypeToken<ArrayList<Similar>>() {}.type
        ) ?: emptyList()
}