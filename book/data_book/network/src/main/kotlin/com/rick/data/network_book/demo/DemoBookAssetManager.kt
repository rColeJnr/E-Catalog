package com.rick.data.network_book.demo

import java.io.InputStream

fun interface DemoBookAssetManager {
    fun open(filename: String): InputStream
}