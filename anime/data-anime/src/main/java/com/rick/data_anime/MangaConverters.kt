package com.rick.data_anime

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.rick.core.JsonParser
import com.rick.data_anime.model_jikan.Author
import com.rick.data_anime.model_jikan.Serialization
import com.rick.data_anime.model_manga.*

@ProvidedTypeConverter
class MangaConverters(
    private val jsonParser: JsonParser
) {

    @TypeConverter
    fun fromImages(obj: Images): String {
        return jsonParser.toJson(
            obj,
            object : TypeToken<Images>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toImages(obj: String): Images {
        return jsonParser.fromJson<Images>(
            obj,
            object : TypeToken<Images>() {}.type
        )!!
    }

    @TypeConverter
    fun fromPublished(obj: Published): String {
        return jsonParser.toJson(
            obj,
            object : TypeToken<Published>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toPublished(obj: String): Published {
        return jsonParser.fromJson<Published>(
            obj,
            object : TypeToken<Published>() {}.type
        )!!
    }

    @TypeConverter
    fun fromAuthor(obj: List<Author>): String {
        return jsonParser.toJson(
            obj,
            object : TypeToken<List<Author>>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toAuthor(obj: String): List<Author> {
        return jsonParser.fromJson<List<Author>>(
            obj,
            object : TypeToken<List<Author>>() {}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun fromSerialization(obj: List<Serialization>): String {
        return jsonParser.toJson(
            obj,
            object : TypeToken<List<Serialization>>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toSerialization(obj: String): List<Serialization> {
        return jsonParser.fromJson<List<Serialization>>(
            obj,
            object : TypeToken<List<Serialization>>() {}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun fromGenres(obj: List<Genre>): String {
        return jsonParser.toJson(
            obj,
            object : TypeToken<List<Genre>>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toGenres(obj: String): List<Genre> {
        return jsonParser.fromJson<List<Genre>>(
            obj,
            object : TypeToken<List<Genre>>() {}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun fromThemes(obj: List<Theme>): String {
        return jsonParser.toJson(
            obj,
            object : TypeToken<List<Theme>>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toThemes(obj: String): List<Theme> {
        return jsonParser.fromJson<List<Theme>>(
            obj,
            object : TypeToken<List<Theme>>() {}.type
        ) ?: emptyList()
    }

}