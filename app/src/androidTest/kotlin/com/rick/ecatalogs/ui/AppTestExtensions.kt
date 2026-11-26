package com.rick.ecatalogs.ui

import android.Manifest
import android.os.Build
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.work.Configuration
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.rick.ecatalogs.MainActivity
import com.rick.ecatalogs.R
import org.hamcrest.Matchers.allOf
import kotlin.test.assertEquals

/** Grants POST_NOTIFICATIONS so MainActivity's one-time permission dialog never shows. */
fun grantNotificationPermission(): GrantPermissionRule =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        GrantPermissionRule.grant()
    }

/**
 * Swaps WorkManager for the test one (nothing runs unless a test drives it), so Settings' sync
 * switch never starts a real sync. Call before launching the activity.
 */
fun useTestWorkManager() {
    WorkManagerTestInitHelper.initializeTestWorkManager(
        InstrumentationRegistry.getInstrumentation().targetContext,
        Configuration.Builder().setExecutor(SynchronousExecutor()).build(),
    )
}

fun string(@StringRes id: Int, vararg args: Any): String =
    InstrumentationRegistry.getInstrumentation().targetContext.getString(id, *args)

val ActivityScenario<MainActivity>.navController: NavController
    get() {
        lateinit var controller: NavController
        onActivity { activity ->
            controller = (activity.supportFragmentManager
                .findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController
        }
        return controller
    }

val ActivityScenario<MainActivity>.currentDestinationId: Int?
    get() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        return navController.currentDestination?.id
    }

fun ActivityScenario<MainActivity>.assertDestination(@IdRes expected: Int) =
    assertEquals(
        expected,
        currentDestinationId,
        "expected ${resourceName(expected)}, was ${currentDestinationId?.let(::resourceName)}",
    )

/** Opens the drawer and taps the item titled [title] (espresso-contrib isn't in the Gradle cache). */
fun ActivityScenario<MainActivity>.selectDrawerItem(@StringRes title: Int) {
    openDrawer()
    onView(allOf(withText(title), isDescendantOfA(withId(R.id.nav_view)))).perform(click())
    // The drawer closes with an animation Espresso doesn't wait for
    awaitDrawer(open = false)
}

/**
 * Opens the drawer without its slide animation, which Espresso doesn't wait for: a tap made
 * mid-slide was lost on API 37, so the next screen never opened.
 */
fun ActivityScenario<MainActivity>.openDrawer() {
    onActivity { it.findViewById<DrawerLayout>(R.id.drawer_layout).openDrawer(GravityCompat.START, false) }
    awaitDrawer(open = true)
}

/** Waits until the drawer is fully open (or fully closed). */
fun ActivityScenario<MainActivity>.awaitDrawer(open: Boolean, timeoutMillis: Long = 5_000) {
    val deadline = System.currentTimeMillis() + timeoutMillis
    while (true) {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        var settled = false
        onActivity {
            val drawer = it.findViewById<DrawerLayout>(R.id.drawer_layout)
            val navView = it.findViewById<android.view.View>(R.id.nav_view)
            settled = if (open) drawer.isDrawerOpen(GravityCompat.START) && navView.x >= 0f
            else !drawer.isDrawerVisible(GravityCompat.START)
        }
        if (settled) return
        if (System.currentTimeMillis() > deadline) {
            throw AssertionError("drawer still ${if (open) "closed" else "open"} after ${timeoutMillis}ms")
        }
        Thread.sleep(50)
    }
}

/** The activity toolbar's title (`toolbar_text`), which top-level screens show. */
fun assertToolbarTitle(@StringRes title: Int) {
    onView(allOf(withId(R.id.toolbar_text), withText(title))).check(matches(isDisplayed()))
}

private fun resourceName(id: Int): String = runCatching {
    InstrumentationRegistry.getInstrumentation().targetContext.resources.getResourceEntryName(id)
}.getOrDefault(id.toString())
