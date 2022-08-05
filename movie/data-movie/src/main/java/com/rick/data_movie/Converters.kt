package com.rick.data_movie

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.rick.core.JsonParser

@ProvidedTypeConverter
class Converters    (
    private val jsonParser: JsonParser
) {

    // convert json back to List<Result>
    @TypeConverter
    fun fromFeatureJson(json: String): List<Movie> =
        jsonParser.fromJson<ArrayList<Movie>>(
            json,
            object : TypeToken<ArrayList<Movie>>(){}.type
        ) ?: emptyList()

    // convert List<Result> to json
    @TypeConverter
    fun toFeatureJson(features: List<Movie>): String =
        jsonParser.toJson(
            features,
            object : TypeToken<ArrayList<Movie>>(){}.type
        ) ?: "[]"

}