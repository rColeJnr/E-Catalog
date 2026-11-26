package com.rick.ecatalogs

import androidx.test.platform.app.InstrumentationRegistry
import com.rick.ecatalogs.data.datastore.ListCache
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import kotlin.test.assertFalse
import kotlin.test.assertIs

/** EcsTestRunner swaps the app for HiltTestApplication, and the app's Hilt graph still builds. */
@HiltAndroidTest
class EcsTestRunnerTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var listCache: ListCache

    @Before
    fun setUp() = hiltRule.inject()

    @Test
    fun runner_usesHiltTestApplication() {
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        assertIs<HiltTestApplication>(app)
    }

    @Test
    fun appGraph_injectsBindings() = runTest {
        assertFalse(listCache.isFresh("ecs_test_runner_never_downloaded"))
    }
}
