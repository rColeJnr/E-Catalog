package com.rick.data.network_movie.demo

import java.io.InputStream

fun interface DemoMovieAssetManager {
    fun open(filename: String): InputStream
}