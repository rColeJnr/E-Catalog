package com.rick.data_movie.imdb

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

    @TypeConverter
    fun fromStringToActorList(actorList: String): List<Actor> =
        jsonParser.fromJson<ArrayList<Actor>>(actorList,
            object : TypeToken<ArrayList<Actor>>() {}.type
        ) ?: emptyList()

    @TypeConverter
    fun fromTvSeriesInfoToString(info: TvSeriesInfo?): String =
        jsonParser.toJson(
            info,
            object : TypeToken<TvSeriesInfo?>() {}.type
        ) ?: "[]"

    @TypeConverter
    fun fromStringToTvSeriesInfo(info: String): TvSeriesInfo? =
        jsonParser.fromJson<TvSeriesInfo>(info,
            object : TypeToken<TvSeriesInfo>() {}.type
        )

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
    fun fromImagesToString(images: Image): String =
        jsonParser.toJson(
            images,
            object : TypeToken<Image>() {}.type
        ) ?: "[]"

    @TypeConverter
    fun fromStringToImages(images: String): Image =
        jsonParser.fromJson<Image>(images,
            object : TypeToken<Image>() {}.type
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