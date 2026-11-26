package com.rick.ecatalogs.benchmarks.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.rick.ecatalogs.benchmarks.PACKAGE_NAME
import com.rick.ecatalogs.benchmarks.catalog.TRENDING_LIST
import com.rick.ecatalogs.benchmarks.catalog.browseCatalog
import com.rick.ecatalogs.benchmarks.catalog.openFirstTitleAndBack
import com.rick.ecatalogs.benchmarks.catalog.scrollCatalog
import com.rick.ecatalogs.benchmarks.catalog.waitForCatalog
import com.rick.ecatalogs.benchmarks.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test

/** Trending Series and Trending Movies: scrolling the list and opening a title. */
class MoviesBaselineProfile {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(PACKAGE_NAME) {
        startActivityAndAllowNotifications()
        waitForCatalog()
        scrollCatalog()
        openFirstTitleAndBack(TRENDING_LIST)

        browseCatalog("Trending Movies", TRENDING_LIST, openTitle = true)
    }
}
