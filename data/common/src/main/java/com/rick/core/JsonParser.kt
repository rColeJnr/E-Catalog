package com.rick.core

import org.json.JSONObject
import java.lang.reflect.Type

interface JsonParser {
    fun <T> fromJson(json: String, type: Type): T?

    fun <T> toJson(obj: T, type: Type): String?

    fun <T> toJsonObject(obj: T, type: Type) : JSONObject
}