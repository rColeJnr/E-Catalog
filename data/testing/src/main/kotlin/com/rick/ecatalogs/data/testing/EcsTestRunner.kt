package com.rick.ecatalogs.data.testing

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Runs instrumented tests in a [HiltTestApplication] instead of the app's own
 * `@HiltAndroidApp` class, so `@HiltAndroidTest` tests get their own Hilt components (and can
 * swap modules with `@TestInstallIn`). Set as `testInstrumentationRunner` by the feature
 * convention plugin and in `:app`.
 */
class EcsTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, name: String, context: Context): Application =
        super.newApplication(cl, HiltTestApplication::class.java.name, context)
}
