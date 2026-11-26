package com.rick.ecatalogs.benchmarks.startup

import androidx.benchmark.macro.BaselineProfileMode.Disable
import androidx.benchmark.macro.BaselineProfileMode.Require
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode.COLD
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rick.ecatalogs.benchmarks.BaselineProfileMetrics
import com.rick.ecatalogs.benchmarks.PACKAGE_NAME
import com.rick.ecatalogs.benchmarks.allowNotifications
import com.rick.ecatalogs.benchmarks.startActivityAndAllowNotifications
import com.rick.ecatalogs.benchmarks.catalog.waitForCatalog
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Cold startup, to Trending Series showing, with and without the Baseline Profile. Compare
 * `startupPrecompiledWithBaselineProfile` with `startupWithoutPreCompilation`.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupWithoutPreCompilation() = startup(CompilationMode.None())

    @Test
    fun startupWithPartialCompilationAndDisabledBaselineProfile() = startup(
        CompilationMode.Partial(baselineProfileMode = Disable, warmupIterations = 1),
    )

    @Test
    fun startupPrecompiledWithBaselineProfile() =
        startup(CompilationMode.Partial(baselineProfileMode = Require))

    @Test
    fun startupFullyPrecompiled() = startup(CompilationMode.Full())

    private fun startup(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = BaselineProfileMetrics.allMetrics,
        compilationMode = compilationMode,
        iterations = 10,
        startupMode = COLD,
        setupBlock = {
            pressHome()
            allowNotifications()
        },
    ) {
        startActivityAndAllowNotifications() // opens the catalog (a fresh install is signed out)
        waitForCatalog()
    }
}
