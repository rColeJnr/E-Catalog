package com.rick.ecatalogs.data.common

fun getListAsString(list: List<Any>): String {
    val sb = StringBuilder()

    list.forEach { sb.append("$it ") }
    return sb.toString()
}
