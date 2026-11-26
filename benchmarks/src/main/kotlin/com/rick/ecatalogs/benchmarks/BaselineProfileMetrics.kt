package com.rick.ecatalogs.benchmarks

import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.TraceSectionMetric

/** Metrics that show whether a Baseline Profile is working (as in Now in Android). */
object BaselineProfileMetrics {
    /** Time spent JIT compiling: goes down when the profile is applied. */
    @OptIn(ExperimentalMetricApi::class)
    val jitCompilationMetric = TraceSectionMetric("JIT Compiling %", label = "JIT compilation")

    /** Time spent initializing classes: goes down when the profile is applied. */
    @OptIn(ExperimentalMetricApi::class)
    val classInitMetric = TraceSectionMetric("L%/%;", label = "ClassInit")

    @OptIn(ExperimentalMetricApi::class)
    val allMetrics = listOf(StartupTimingMetric(), jitCompilationMetric, classInitMetric)
}
