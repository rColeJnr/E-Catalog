package com.rick.book.data_book.network.demo

import java.io.InputStream

fun interface DemoBookAssetManager {
    fun open(filename: String): InputStream
}