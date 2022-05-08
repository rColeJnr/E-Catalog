package com.rick.moviecatalog.data.util

import com.google.gson.Gson
import org.json.JSONObject
import java.lang.reflect.Type

class GsonParser(
    private val gson: Gson
) : JsonParser {

    override fun <T> fromJson(json: String, type: Type): T? {
        return gson.fromJson(json, type)
    }

    override fun <T> toJson(obj: T, type: Type): String? {
        return gson.toJson(obj, type)
    }

    override fun <T> toJsonObject(obj: T, type: Type): JSONObject {
        return JSONObject(toJson(obj, type)!!)
    }
}