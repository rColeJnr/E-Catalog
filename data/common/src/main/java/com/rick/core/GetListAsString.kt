package com.rick.core

fun getListAsString(list: List<Any>): String {
    val sb = StringBuilder()

    list.forEach { sb.append("$it ") }
    return sb.toString()
}
