package com.rick.ecatalogs.movie.screen_movie.common.util

/**
 * TMDB image URL sized for full-width phone layouts. Avoid "original": those files are up to
 * ~2000x3000 px, several MB each, and Glide/Coil downsample them to view size anyway.
 */
fun getTmdbImageUrl(url: String): String =
    "https://image.tmdb.org/t/p/w780$url"
