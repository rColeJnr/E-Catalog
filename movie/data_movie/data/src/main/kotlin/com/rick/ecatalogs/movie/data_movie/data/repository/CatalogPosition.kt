package com.rick.ecatalogs.movie.data_movie.data.repository

/** Room for a page's items: TMDB pages hold 20, NYT 10. */
private const val CATALOG_PAGE_STRIDE = 1_000

/**
 * The catalog_position of the [index]th item of [page]: the catalog lists keep the API's order,
 * pages after pages, whichever page a refresh starts from.
 */
internal fun catalogPosition(page: Int, index: Int): Int = page * CATALOG_PAGE_STRIDE + index
