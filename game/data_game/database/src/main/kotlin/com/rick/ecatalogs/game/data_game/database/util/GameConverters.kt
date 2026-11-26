package com.rick.ecatalogs.game.data_game.database.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.datetime.Instant

class GameConverters {

    private val gson = Gson()
    private val stringList = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromStringList(value: List<String>): String = gson.toJson(value, stringList)

    @TypeConverter
    fun toStringList(value: String): List<String> =
        gson.fromJson<List<String>>(value, stringList) ?: emptyList()

    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilliseconds()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let(Instant::fromEpochMilliseconds)
}
