package com.rick.ecatalogs.movie.screen_movie.common.util

/**
 * TMDB image URL sized for full-width phone layouts. Avoid "original": those files are up to
 * ~2000x3000 px, several MB each, and Glide/Coil downsample them to view size anyway.
 */
// [size]: w92 / w185 for logos and faces, w780 (default) for posters and backdrops.
fun getTmdbImageUrl(url: String, size: String = "w780"): String =
    "https://image.tmdb.org/t/p/$size$url"
