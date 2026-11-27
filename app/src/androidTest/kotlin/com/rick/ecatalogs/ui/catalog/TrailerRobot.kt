package com.rick.ecatalogs.ui.catalog

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.platform.app.InstrumentationRegistry
import com.rick.ecatalogs.data.trailer.TAG_GALLERY
import com.rick.ecatalogs.data.trailer.TAG_PLAY_TRAILER
import com.rick.ecatalogs.data.trailer.TrailerActivity
import com.rick.ecatalogs.ui.string
import org.hamcrest.Matcher
import java.util.concurrent.CopyOnWriteArrayList
import com.rick.ecatalogs.data.trailer.R as TrailerR

/**
 * Records the trailer screen's launches and blocks them, so tests check what would play without
 * playing anything (no video streams on the emulator's connection).
 */
@RequiresApi(Build.VERSION_CODES.O)
private class TrailerLaunches : Instrumentation.ActivityMonitor() {
    val videoIds = CopyOnWriteArrayList<String>()

    override fun onStartActivity(intent: Intent): Instrumentation.ActivityResult? {
        if (intent.component?.className != TrailerActivity::class.java.name) return null
        videoIds += intent.getStringExtra(TrailerActivity.EXTRA_VIDEO_ID).orEmpty()
        return Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null)
    }
}

/** Runs [block]; returns the video ids of the trailer screens it opened (which aren't started). */
@RequiresApi(Build.VERSION_CODES.O)
fun trailerLaunchesDuring(block: () -> Unit): List<String> {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val launches = TrailerLaunches()
    instrumentation.addMonitor(launches)
    try {
        block()
        instrumentation.waitForIdleSync()
    } finally {
        instrumentation.removeMonitor(launches)
    }
    return launches.videoIds.toList()
}

/**
 * Checks a details header gallery of [pages] pages: it starts on the trailer (its thumbnail and
 * play button, not playing), and one swipe shows the next page (the poster).
 */
fun ComposeTestRule.assertGalleryStartsWithTrailer(pages: Int) {
    waitUntil(15_000) { onAllNodesWithTag(TAG_PLAY_TRAILER).fetchSemanticsNodes().isNotEmpty() }
    onNodeWithTag(TAG_PLAY_TRAILER).assertIsDisplayed()
    awaitPage(1, pages)

    onNodeWithTag(TAG_GALLERY).performTouchInput { swipeLeft() }

    awaitPage(2, pages)
}

private fun ComposeTestRule.awaitPage(page: Int, pages: Int) {
    val description = string(TrailerR.string.data_trailer_page_of, page, pages)
    waitUntil(5_000) { onAllNodesWithContentDescription(description).fetchSemanticsNodes().isNotEmpty() }
}

/**
 * Scrolls the view into sight in any scrolling parent (Espresso's scrollTo() doesn't take
 * NestedScrollView, which the details screens use).
 */
fun bringIntoView(): ViewAction = object : ViewAction {
    override fun getConstraints(): Matcher<View> = withEffectiveVisibility(Visibility.VISIBLE)
    override fun getDescription() = "bring into view"
    override fun perform(uiController: UiController, view: View) {
        view.requestRectangleOnScreen(Rect(0, 0, view.width, view.height), true)
        uiController.loopMainThreadUntilIdle()
    }
}
