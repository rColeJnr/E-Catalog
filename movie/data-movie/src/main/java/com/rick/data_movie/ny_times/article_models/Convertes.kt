package com.rick.data_movie.ny_times.article_models

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.rick.core.JsonParser

@ProvidedTypeConverter
class Converters (
    private val jsonParser: JsonParser
){

    @TypeConverter
    fun fromMultimedia(multimedia: Multimedia): String =
        jsonParser.toJson(
            multimedia,
            object: TypeToken<Multimedia>() {}.type
        ) ?: "[]"

    @TypeConverter
    fun toMultimedia(json: String): Multimedia =
        jsonParser.fromJson<Multimedia>(
            json,
            object: TypeToken<Multimedia>() {}.type
        )!!

    @TypeConverter
    fun fromHeadline(headline: Headline): String =
        jsonParser.toJson(
            headline,
            object : TypeToken<Headline>() {}.type
        ) ?: ""

    @TypeConverter
    fun toHeadline(json: String): Headline =
        jsonParser.fromJson<Headline>(
            json,
            object : TypeToken<Headline>() {}.type
        )!!

    @TypeConverter
    fun fromKeyword(keywords: Keyword): String =
        jsonParser.toJson(
            keywords,
            object: TypeToken<Keyword>() {}.type
        )  ?: ""

    @TypeConverter
    fun toKeyword(json: String): Keyword =
        jsonParser.fromJson<Keyword>(
            json,
            object: TypeToken<Keyword>() {}.type
        )!!

    @TypeConverter
    fun fromByline(byline: Byline): String =
        jsonParser.toJson(
            byline,
            object : TypeToken<Byline>() {}.type
        ) ?: ""

    @TypeConverter
    fun toByline(json: String): Byline =
        jsonParser.fromJson<Byline>(
            json,
            object : TypeToken<Byline>() {}.type
        )!!

}