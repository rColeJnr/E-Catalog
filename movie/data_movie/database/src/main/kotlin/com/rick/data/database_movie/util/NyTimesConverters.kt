package com.rick.data.database_movie.util

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.rick.core.JsonParser
import com.rick.data.model_movie.article_models.Byline
import com.rick.data.model_movie.article_models.Headline
import com.rick.data.model_movie.article_models.Multimedia
import com.rick.data.model_movie.tmdb.movie.Genre
import com.rick.data.model_movie.tmdb.series.Creator
import com.rick.data.model_movie.tmdb.series.LastEpisodeToAir
import kotlinx.datetime.Instant

@ProvidedTypeConverter
class NyTimesConverters(
    private val jsonParser: JsonParser
) {

    @TypeConverter
    fun fromMultimedia(multimedia: Multimedia): String =
        jsonParser.toJson(
            multimedia,
            object : TypeToken<Multimedia>() {}.type
        ) ?: "[]"

    @TypeConverter
    fun toMultimedia(json: String): Multimedia =
        jsonParser.fromJson<Multimedia>(
            json,
            object : TypeToken<Multimedia>() {}.type
        )!!

    @TypeConverter
    fun fromListMultimedia(multimedia: List<Multimedia>): String =
        jsonParser.toJson(
            multimedia,
            object : TypeToken<List<Multimedia>>() {}.type
        ) ?: "[]"

    @TypeConverter
    fun toListMultimedia(json: String): List<Multimedia> =
        jsonParser.fromJson<List<Multimedia>>(
            json,
            object : TypeToken<List<Multimedia>>() {}.type
        ) ?: emptyList()

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


    // TMDBConverters
    @TypeConverter
    fun fromCreatorList(value: List<Creator>): String {
        return jsonParser.toJson(
            value,
            object : TypeToken<List<Creator>>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toCreatorList(value: String): List<Creator> {
        return jsonParser.fromJson(
            value,
            object : TypeToken<List<Creator>>() {}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun fromCreator(value: Creator): String {
        return jsonParser.toJson(
            value,
            object : TypeToken<Creator>() {}.type
        ) ?: ""
    }

    @TypeConverter
    fun toCreator(value: String): Creator {
        return jsonParser.fromJson(
            value,
            object : TypeToken<Creator>() {}.type
        )!!
    }

    @TypeConverter
    fun fromGenreList(value: List<Genre>): String {
        return jsonParser.toJson(
            value,
            object : TypeToken<List<Genre>>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toGenreList(value: String): List<Genre> {
        return jsonParser.fromJson(
            value,
            object : TypeToken<List<Genre>>() {}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun fromGenre(value: Genre): String {
        return jsonParser.toJson(
            value,
            object : TypeToken<Genre>() {}.type
        ) ?: ""
    }

    @TypeConverter
    fun toGenre(value: String): Genre {
        return jsonParser.fromJson(
            value,
            object : TypeToken<Genre>() {}.type
        )!!
    }

    @TypeConverter
    fun fromLastEpisodeToAir(value: LastEpisodeToAir): String {
        return jsonParser.toJson(
            value,
            object : TypeToken<LastEpisodeToAir>() {}.type
        ) ?: ""
    }

    @TypeConverter
    fun toLastEpisodeToAir(value: String): LastEpisodeToAir {
        return jsonParser.fromJson(
            value,
            object : TypeToken<LastEpisodeToAir>() {}.type
        )!!
    }

    @TypeConverter
    fun longToInstant(value: Long?): Instant? =
        value?.let(Instant::fromEpochMilliseconds)

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? =
        instant?.toEpochMilliseconds()
}