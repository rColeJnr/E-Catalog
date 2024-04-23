package com.rick.data.database_anime.util

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.rick.core.JsonParser
import com.rick.data.model_anime.model_jikan.Aired
import com.rick.data.model_anime.model_jikan.Author
import com.rick.data.model_anime.model_jikan.Genre
import com.rick.data.model_anime.model_jikan.Images
import com.rick.data.model_anime.model_jikan.Published
import com.rick.data.model_anime.model_jikan.Serialization
import com.rick.data.model_anime.model_jikan.Theme
import com.rick.data.model_anime.model_jikan.Trailer

@ProvidedTypeConverter
class JikanConverters(
    private val jsonParser: JsonParser
) {
    @TypeConverter
    fun fromSerialization(obj: List<Serialization>?): String {
        return jsonParser.toJson(
            obj,
            object : TypeToken<List<Serialization>?>() {}.type
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
    fun fromPublished(obj: Published?): String {
        return jsonParser.toJson(
            obj,
            object : TypeToken<Published?>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toPublished(obj: String): Published? {
        return jsonParser.fromJson<Published>(
            obj,
            object : TypeToken<Published>() {}.type
        )
    }

    @TypeConverter
    fun fromAuthor(obj: List<Author>?): String {
        return jsonParser.toJson(
            obj,
            object : TypeToken<List<Author>?>() {}.type
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
    fun fromImages(images: Images): String {
        return jsonParser.toJson(
            images,
            object : TypeToken<Images>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toImages(images: String): Images {
        return jsonParser.fromJson<Images>(
            images,
            object : TypeToken<Images>() {}.type
        )!!
    }

    @TypeConverter
    fun fromTrailer(obj: Trailer?): String {
        return jsonParser.toJson(
            obj,
            object : TypeToken<Trailer?>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toTrailer(obj: String): Trailer? {
        return jsonParser.fromJson<Trailer?>(
            obj,
            object : TypeToken<Trailer?>() {}.type
        )
    }

    @TypeConverter
    fun fromAired(obj: Aired?): String {
        return jsonParser.toJson(
            obj,
            object : TypeToken<Aired?>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toAired(obj: String): Aired? {
        return jsonParser.fromJson<Aired>(
            obj,
            object : TypeToken<Aired>() {}.type
        )
    }

    @TypeConverter
    fun fromGenre(obj: List<Genre>): String {
        return jsonParser.toJson(
            obj,
            object : TypeToken<List<Genre>>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toGenre(obj: String): List<Genre> {
        return jsonParser.fromJson<List<Genre>>(
            obj,
            object : TypeToken<List<Genre>>() {}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun fromThemes(obj: List<Theme>?): String {
        return jsonParser.toJson(
            obj,
            object : TypeToken<List<Theme>?>() {}.type
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