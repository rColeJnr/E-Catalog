package com.rick.ecatalogs.benchmarks.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.rick.ecatalogs.benchmarks.PACKAGE_NAME
import com.rick.ecatalogs.benchmarks.catalog.JIKAN_LIST
import com.rick.ecatalogs.benchmarks.catalog.browseCatalog
import com.rick.ecatalogs.benchmarks.catalog.waitForCatalog
import com.rick.ecatalogs.benchmarks.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test

/** Anime and Manga catalogs, and an anime's details. */
class AnimeBaselineProfile {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(PACKAGE_NAME) {
        startActivityAndAllowNotifications()
        waitForCatalog()

        browseCatalog("Anime", JIKAN_LIST, openTitle = true)
        browseCatalog("Manga", JIKAN_LIST, openTitle = false)
    }
}
