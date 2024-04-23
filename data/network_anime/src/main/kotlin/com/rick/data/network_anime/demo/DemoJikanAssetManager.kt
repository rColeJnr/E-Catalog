package com.rick.data.network_anime.demo

import java.io.InputStream

fun interface DemoJikanAssetManager {
    fun open(filename: String): InputStream
}