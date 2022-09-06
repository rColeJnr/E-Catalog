package com.rick.data_movie.ny_times

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.rick.core.JsonParser
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

}