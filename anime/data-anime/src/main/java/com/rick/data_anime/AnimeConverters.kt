package com.rick.data_anime

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.rick.core.JsonParser
import com.rick.data_anime.model_jikan.Aired
import com.rick.data_anime.model_jikan.Genre
import com.rick.data_anime.model_jikan.Images
import com.rick.data_anime.model_jikan.Trailer

@ProvidedTypeConverter
class AnimeConverters(
    private val jsonParser: JsonParser
) {

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
    fun fromTrailer(obj: Trailer): String {
        return jsonParser.toJson(
            obj,
            object : TypeToken<Trailer>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toTrailer(obj: String): Trailer {
        return jsonParser.fromJson<Trailer>(
            obj,
            object : TypeToken<Trailer>() {}.type
        )!!
    }

    @TypeConverter
    fun fromAired(obj: Aired): String {
        return jsonParser.toJson(
            obj,
            object : TypeToken<Aired>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toAired(obj: String): Aired {
        return jsonParser.fromJson<Aired>(
            obj,
            object : TypeToken<Aired>() {}.type
        )!!
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
}