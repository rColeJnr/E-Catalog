package com.rick.moviecatalog.data.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.rick.moviecatalog.data.model.Result
import com.rick.moviecatalog.data.util.JsonParser

@ProvidedTypeConverter
class Converters(
    private val jsonParser: JsonParser
) {

    // convert json back to List<Result>
    @TypeConverter
    fun fromFeatureJson(json: String): List<Result> =
        jsonParser.fromJson<ArrayList<Result>>(
            json,
            object : TypeToken<ArrayList<Result>>(){}.type
        ) ?: emptyList()

    // convert List<Result> to json
    @TypeConverter
    fun toFeatureJson(features: List<Result>): String =
        jsonParser.toJson(
            features,
            object : TypeToken<ArrayList<Result>>(){}.type
        ) ?: "[]"

}