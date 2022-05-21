package com.rick.core

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class Converters <T> (
    private val jsonParser: JsonParser
) {

    // convert json back to List<Result>
    @TypeConverter
    fun fromFeatureJson(json: String): List<T> =
        jsonParser.fromJson<ArrayList<T>>(
            json,
            object : TypeToken<ArrayList<T>>(){}.type
        ) ?: emptyList()

    // convert List<Result> to json
    @TypeConverter
    fun toFeatureJson(features: List<T>): String =
        jsonParser.toJson(
            features,
            object : TypeToken<ArrayList<T>>(){}.type
        ) ?: "[]"

}