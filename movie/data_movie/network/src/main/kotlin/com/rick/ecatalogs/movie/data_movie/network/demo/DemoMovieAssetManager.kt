package com.rick.ecatalogs.movie.data_movie.network.demo

import java.io.InputStream

fun interface DemoMovieAssetManager {
    fun open(filename: String): InputStream
}