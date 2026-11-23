package com.rick.ecatalogs.anime.data_anime.network.demo

import java.io.InputStream

fun interface DemoJikanAssetManager {
    fun open(filename: String): InputStream
}