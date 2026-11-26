package com.rick.ecatalogs.benchmarks.catalog

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.Until
import com.rick.ecatalogs.benchmarks.flingElementDownUp
import com.rick.ecatalogs.benchmarks.openFromDrawer
import com.rick.ecatalogs.benchmarks.res
import com.rick.ecatalogs.benchmarks.untilHasChildren
import com.rick.ecatalogs.benchmarks.waitAndFindObject

/** List ids of the catalog screens. */
const val TRENDING_LIST = "recyclerView" // Trending series/movies, both book screens
const val JIKAN_LIST = "jikan_recycler_view" // Anime, manga
private const val TITLE_CARD = "movie_entry_card_view" // A movie/series/anime/manga entry

/**
 * Waits for a catalog list to show entries (the journeys open the app on Trending Series).
 */
fun MacrobenchmarkScope.waitForCatalog(listId: String = TRENDING_LIST) {
    val list = device.waitAndFindObject(res(listId), 15_000)
    // Network content: give the first page time to arrive
    list.wait(untilHasChildren(2), 30_000)
    device.waitForIdle()
}

fun MacrobenchmarkScope.scrollCatalog(listId: String = TRENDING_LIST) {
    device.flingElementDownUp(device.waitAndFindObject(res(listId)))
}

/** Opens the first title on screen, lets its details load, and comes back. */
fun MacrobenchmarkScope.openFirstTitleAndBack(listId: String) {
    val card = device.wait(Until.findObject(res(TITLE_CARD)), 10_000) ?: return
    card.click()
    device.wait(Until.gone(res(listId)), 5_000)
    device.waitForIdle()
    // Details are a long page: scroll to load its sections (cast, recommendations…)
    device.swipe(
        device.displayWidth / 2, device.displayHeight * 3 / 4,
        device.displayWidth / 2, device.displayHeight / 4, 20,
    )
    device.waitForIdle()
    // Back to the list. A press can be taken by something the scroll opened on the details
    // page (e.g. a season sheet), so press again until the list shows.
    repeat(MAX_BACK_PRESSES) {
        device.pressBack()
        if (device.wait(Until.hasObject(res(listId)), 5_000)) {
            device.waitForIdle()
            return
        }
    }
    device.waitAndFindObject(res(listId)) // fails the run with a clear message
}

private const val MAX_BACK_PRESSES = 3

/** Opens a catalog from the drawer by its English title and browses it. */
fun MacrobenchmarkScope.browseCatalog(drawerTitle: String, listId: String, openTitle: Boolean) {
    openFromDrawer(drawerTitle)
    waitForCatalog(listId)
    scrollCatalog(listId)
    if (openTitle) openFirstTitleAndBack(listId)
}
