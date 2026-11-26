package com.rick.ecatalogs.anime.data_anime.data.repository

/** Room for a page's items: Jikan pages hold 25. */
private const val CATALOG_PAGE_STRIDE = 1_000

/**
 * The catalog_position of the [index]th item of [page]: the catalog lists keep the API's order,
 * pages after pages, whichever page a refresh starts from.
 */
internal fun catalogPosition(page: Int, index: Int): Int = page * CATALOG_PAGE_STRIDE + index
