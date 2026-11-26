package com.rick.ecatalogs.benchmarks.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.rick.ecatalogs.benchmarks.PACKAGE_NAME
import com.rick.ecatalogs.benchmarks.catalog.waitForCatalog
import com.rick.ecatalogs.benchmarks.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test

/**
 * App startup up to Trending Series showing. Also a startup profile
 * (`includeInStartupProfile`), used for Dex Layout Optimizations.
 */
class StartupBaselineProfile {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(PACKAGE_NAME, includeInStartupProfile = true) {
        startActivityAndAllowNotifications()
        waitForCatalog()
    }
}
