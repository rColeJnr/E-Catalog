package com.rick.ecatalogs.benchmarks.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.rick.ecatalogs.benchmarks.PACKAGE_NAME
import com.rick.ecatalogs.benchmarks.catalog.TRENDING_LIST
import com.rick.ecatalogs.benchmarks.catalog.browseCatalog
import com.rick.ecatalogs.benchmarks.catalog.waitForCatalog
import com.rick.ecatalogs.benchmarks.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test

/** NY Times Bestsellers and the Gutenberg Project catalogs. */
class BooksBaselineProfile {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(PACKAGE_NAME) {
        startActivityAndAllowNotifications()
        waitForCatalog()

        browseCatalog("NY Times Bestsellers", TRENDING_LIST, openTitle = false)
        browseCatalog("Gutenberg Project", TRENDING_LIST, openTitle = false)
    }
}
