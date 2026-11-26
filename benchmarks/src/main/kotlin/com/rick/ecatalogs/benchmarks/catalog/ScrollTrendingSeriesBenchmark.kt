package com.rick.ecatalogs.benchmarks.catalog

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rick.ecatalogs.benchmarks.PACKAGE_NAME
import com.rick.ecatalogs.benchmarks.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Frame timing while scrolling Trending Series (poster cards, browse rows). */
@RunWith(AndroidJUnit4::class)
class ScrollTrendingSeriesBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollTrendingSeriesCompilationNone() = scroll(CompilationMode.None())

    @Test
    fun scrollTrendingSeriesCompilationBaselineProfile() = scroll(CompilationMode.Partial())

    private fun scroll(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = compilationMode,
        iterations = 10,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityAndAllowNotifications()
            waitForCatalog()
        },
    ) {
        scrollCatalog()
    }
}
