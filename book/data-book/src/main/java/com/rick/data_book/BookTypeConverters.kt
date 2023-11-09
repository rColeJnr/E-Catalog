package com.rick.data_book

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.rick.core.JsonParser
import com.rick.data_book.gutenberg.model.Author
import com.rick.data_book.gutenberg.model.Formats
import com.rick.data_book.gutenberg.model.Translator

@ProvidedTypeConverter
class BookTypeConverters(
    private val jsonParser: JsonParser
) {
    @TypeConverter
    fun fromAuthorsToString(authors: List<Author>): String =
        jsonParser.toJson(
            authors,
            object : TypeToken<List<Author>>() {}.type
        ) ?: "[]"

    @TypeConverter
    fun fromStringToAuthors(authors: String): List<Author> =
        jsonParser.fromJson<List<Author>>(
            authors,
            object : TypeToken<List<Author>>() {}.type
        ) ?: emptyList()

    @TypeConverter
    fun fromFormatsToString(formats: Formats): String =
        jsonParser.toJson(
            formats,
            object : TypeToken<Formats>() {}.type
        ) ?: "[]"

    @TypeConverter
    fun fromStringToFormats(formats: String): Formats =
        jsonParser.fromJson<Formats>(
            formats,
            object : TypeToken<Formats>() {}.type
        )!!

    @TypeConverter
    fun fromTranslatorToString(translators: List<Translator>): String =
        jsonParser.toJson(
            translators,
            object : TypeToken<List<Translator>>() {}.type
        ) ?: "[]"

    @TypeConverter
    fun fromStringToTranslators(translators: String): List<Translator> =
        jsonParser.fromJson<List<Translator>>(
            translators,
            object : TypeToken<List<Translator>>() {}.type
        ) ?: emptyList()

    @TypeConverter
    fun fromListToString(list: List<String>): String =
        jsonParser.toJson(
            list,
            object : TypeToken<List<String>>() {}.type
        ) ?: "[]"

    @TypeConverter
    fun fromStringToString(string: String): List<String> =
        jsonParser.fromJson<List<String>>(
            string,
            object : TypeToken<List<String>>() {}.type
        ) ?: emptyList()

}