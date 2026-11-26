package com.rick.ecatalogs.benchmarks

import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.UiObject2Condition
import androidx.test.uiautomator.Until
import java.util.regex.Pattern

/** The app under test, for the flavor being run (release build types add no suffix). */
val PACKAGE_NAME = buildString {
    append("com.rick.moviecatalog")
    append(BuildConfig.APP_FLAVOR_SUFFIX)
}

/**
 * A view by its id name. The app's views live in many modules, so the resource package isn't
 * always the app id: match the name alone ("…:id/recyclerView").
 */
fun res(idName: String): BySelector = By.res(Pattern.compile(".*:id/$idName"))

/** Waits until [selector] is on screen and returns it, or fails with what was on screen. */
fun UiDevice.waitAndFindObject(selector: BySelector, timeout: Long = 10_000): UiObject2 {
    if (!wait(Until.hasObject(selector), timeout)) {
        throw AssertionError("Not on screen after ${timeout}ms: $selector")
    }
    return findObject(selector)
}

/** Flings a list down and back up, away from the screen edges (system gestures). */
fun UiDevice.flingElementDownUp(element: UiObject2) {
    element.setGestureMargin(displayWidth / 5)
    element.fling(Direction.DOWN)
    waitForIdle()
    element.fling(Direction.UP)
    waitForIdle()
}

/** Satisfied once the element has at least [childCount] children (e.g. a list has loaded). */
fun untilHasChildren(childCount: Int = 1): UiObject2Condition<Boolean> =
    object : UiObject2Condition<Boolean>() {
        override fun apply(element: UiObject2): Boolean = element.childCount >= childCount
    }
