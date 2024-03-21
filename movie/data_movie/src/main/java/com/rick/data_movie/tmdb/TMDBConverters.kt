package com.rick.data_movie.tmdb

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.rick.core.JsonParser
import com.rick.data_movie.tmdb.movie.Genre
import com.rick.data_movie.tmdb.tv.Creator
import com.rick.data_movie.tmdb.tv.LastEpisodeToAir
import com.rick.data_movie.tmdb.tv.SimilarResponse

@ProvidedTypeConverter
class TMDBConverters(
    private val jsonParser: JsonParser
) {

    @TypeConverter
    fun fromCreatorList(value: List<Creator>): String {
        return jsonParser.toJson(
            value,
            object: TypeToken<List<Creator>>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toCreatorList(value: String): List<Creator> {
        return jsonParser.fromJson(
            value,
            object: TypeToken<List<Creator>>() {}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun fromCreator(value: Creator): String {
        return jsonParser.toJson(
            value,
            object: TypeToken<Creator>() {}.type
        ) ?: ""
    }

    @TypeConverter
    fun toCreator(value: String): Creator {
        return jsonParser.fromJson(
            value,
            object: TypeToken<Creator>() {}.type
        )!!
    }

    @TypeConverter
    fun fromGenreList(value: List<Genre>): String {
        return jsonParser.toJson(
            value,
            object: TypeToken<List<Genre>>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun toGenreList(value: String): List<Genre> {
        return jsonParser.fromJson(
            value,
            object: TypeToken<List<Genre>>() {}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun fromGenre(value: Genre): String {
        return jsonParser.toJson(
            value,
            object: TypeToken<Genre>() {}.type
        ) ?: ""
    }

    @TypeConverter
    fun toGenre(value: String): Genre {
        return jsonParser.fromJson(
            value,
            object: TypeToken<Genre>() {}.type
        )!!
    }

    @TypeConverter
    fun fromLastEpisodeToAir(value: LastEpisodeToAir): String {
        return jsonParser.toJson(
            value,
            object: TypeToken<LastEpisodeToAir>() {}.type
        ) ?: ""
    }

    @TypeConverter
    fun toLastEpisodeToAir(value: String): LastEpisodeToAir {
        return jsonParser.fromJson(
            value,
            object: TypeToken<LastEpisodeToAir>() {}.type
        )!!
    }

    @TypeConverter
    fun fromTvSimilarResponse(value: SimilarResponse): String {
        return jsonParser.toJson(
            value,
            object: TypeToken<SimilarResponse>() {}.type
        ) ?: ""
    }

    @TypeConverter
    fun toTvSimilarResponse(value: String): SimilarResponse {
        return jsonParser.fromJson(
            value,
            object: TypeToken<SimilarResponse>() {}.type
        )!!
    }


    @TypeConverter
    fun fromMovieSimilarResponse(value: com.rick.data_movie.tmdb.movie.SimilarResponse): String {
        return jsonParser.toJson(
            value,
            object: TypeToken<com.rick.data_movie.tmdb.movie.SimilarResponse>() {}.type
        ) ?: ""
    }

    @TypeConverter
    fun toMovieSimilarResponse(value: String): com.rick.data_movie.tmdb.movie.SimilarResponse {
        return jsonParser.fromJson(
            value,
            object: TypeToken<com.rick.data_movie.tmdb.movie.SimilarResponse>() {}.type
        )!!
    }

}