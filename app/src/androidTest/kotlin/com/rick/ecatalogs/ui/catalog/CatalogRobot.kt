package com.rick.ecatalogs.ui.catalog

import android.graphics.Rect
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.ViewAction
import androidx.test.espresso.UiController
import android.view.MotionEvent
import android.view.InputDevice
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.rick.ecatalogs.MainActivity
import com.rick.ecatalogs.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import kotlin.test.fail

/** How to find one item in a RecyclerView screen: the list, the item card, its title and heart. */
data class ListSpec(
    @IdRes val listId: Int,
    @IdRes val cardId: Int,
    @IdRes val titleId: Int,
    @IdRes val favoriteId: Int? = null,
)

private val instrumentation get() = InstrumentationRegistry.getInstrumentation()

/** Polls [condition] on the main thread until it holds; demo data loads off the main thread. */
fun ActivityScenario<MainActivity>.waitUntil(
    description: String,
    timeoutMillis: Long = 15_000,
    condition: (MainActivity) -> Boolean,
) {
    val deadline = System.currentTimeMillis() + timeoutMillis
    while (System.currentTimeMillis() < deadline) {
        instrumentation.waitForIdleSync()
        var met = false
        onActivity { met = condition(it) }
        if (met) return
        Thread.sleep(100)
    }
    fail("Timed out after ${timeoutMillis}ms waiting for $description")
}

/** The titles of the item cards currently laid out in the list (header rows excluded). */
fun MainActivity.cardTitles(spec: ListSpec): List<String> {
    val list = findViewById<RecyclerView>(spec.listId) ?: return emptyList()
    return (0 until list.childCount).mapNotNull { i ->
        val child = list.getChildAt(i)
        val card = if (child.id == spec.cardId) child else child.findViewById<View>(spec.cardId)
        card?.findViewById<TextView>(spec.titleId)?.text?.toString()?.takeIf(String::isNotBlank)
    }
}

fun ActivityScenario<MainActivity>.awaitCards(spec: ListSpec, atLeast: Int = 1) =
    waitUntil("$atLeast item(s) in the list") { it.cardTitles(spec).size >= atLeast }

/**
 * Brings the [index]-th item (counted after any header adapters) on screen and returns its title.
 * Works for a scrolling RecyclerView and for one inside a NestedScrollView (bestsellers).
 */
fun ActivityScenario<MainActivity>.scrollToItem(spec: ListSpec, index: Int): String {
    var position = -1
    waitUntil("item $index in the adapter") { activity ->
        val list = activity.findViewById<RecyclerView>(spec.listId) ?: return@waitUntil false
        val adapter = list.adapter ?: return@waitUntil false
        val headers = (adapter as? ConcatAdapter)?.adapters?.dropLast(1)?.sumOf { it.itemCount } ?: 0
        position = headers + index
        adapter.itemCount > position
    }
    // A third of the way down when the list scrolls itself: clear of the toolbar overlapping its
    // top, and not at the bottom edge, where Back could leave it just off screen
    onActivity { activity ->
        val list = activity.findViewById<RecyclerView>(spec.listId)
        (list.layoutManager as? androidx.recyclerview.widget.LinearLayoutManager)
            ?.scrollToPositionWithOffset(position, list.height / 3) ?: list.scrollToPosition(position)
    }
    var title: String? = null
    waitUntil("item $index laid out") { activity ->
        val list = activity.findViewById<RecyclerView>(spec.listId)
        val itemView = list.findViewHolderForAdapterPosition(position)?.itemView ?: return@waitUntil false
        // A RecyclerView in a NestedScrollView lays out every item: scroll the parent to it
        itemView.requestRectangleOnScreen(Rect(0, 0, itemView.width, itemView.height), true)
        title = itemView.findViewById<TextView>(spec.titleId)?.text?.toString()?.takeIf(String::isNotBlank)
        title != null
    }
    instrumentation.waitForIdleSync()
    return title!!
}

/** The title view of the card titled [title] in the list. */
fun cardTitle(spec: ListSpec, title: String): Matcher<View> = first(
    allOf(
        withId(spec.titleId),
        withText(title),
        isDescendantOfA(allOf(withId(spec.cardId), isDescendantOfA(withId(spec.listId)))),
    )
)

/** The heart of the card titled [title]. */
fun cardFavorite(spec: ListSpec, title: String): Matcher<View> = first(
    allOf(
        withId(spec.favoriteId ?: error("no favorite id in $spec")),
        isDescendantOfA(
            allOf(withId(spec.cardId), hasDescendant(allOf(withId(spec.titleId), withText(title))))
        ),
    )
)

fun openCard(spec: ListSpec, title: String) {
    // The card, tapped in the middle of its visible part: Espresso's click() needs 90% of the
    // view on screen, and a tall card (anime, manga) or a long title often isn't
    onView(
        first(
            allOf(
                withId(spec.cardId),
                hasDescendant(allOf(withId(spec.titleId), withText(title))),
                isDescendantOfA(withId(spec.listId)),
            )
        )
    ).perform(clickVisiblePart())
}

/** A tap at the centre of the part of the view that's on screen. */
fun clickVisiblePart(): ViewAction = object : ViewAction {
    override fun getConstraints(): Matcher<View> = isDisplayed()
    override fun getDescription() = "tap the visible part"
    override fun perform(uiController: UiController, view: View) {
        GeneralClickAction(
            Tap.SINGLE,
            { target ->
                val visible = Rect()
                target.getGlobalVisibleRect(visible)
                val inWindow = IntArray(2).also(target::getLocationInWindow)
                val onScreen = IntArray(2).also(target::getLocationOnScreen)
                floatArrayOf(
                    visible.exactCenterX() + onScreen[0] - inWindow[0],
                    visible.exactCenterY() + onScreen[1] - inWindow[1],
                )
            },
            Press.FINGER,
            InputDevice.SOURCE_UNKNOWN,
            MotionEvent.BUTTON_PRIMARY,
        ).perform(uiController, view)
    }
}

/**
 * The scroll to compare before and after a round trip: the list's first visible position and the
 * enclosing NestedScrollView's scrollY (bestsellers). Not on-screen pixels: the activity toolbar
 * collapses while scrolling (scroll|enterAlways) and comes back expanded, shifting the page by its
 * height while the list itself stays where it was.
 */
fun MainActivity.scrollPosition(spec: ListSpec): Pair<Int, Int> {
    val list = findViewById<RecyclerView>(spec.listId)
    val layoutManager = list?.layoutManager as? androidx.recyclerview.widget.LinearLayoutManager
    val outer = generateSequence(list?.parent) { it.parent }.filterIsInstance<View>()
        .firstOrNull { it is androidx.core.widget.NestedScrollView }
    return (layoutManager?.findFirstVisibleItemPosition() ?: -1) to (outer?.scrollY ?: 0)
}

/** Where a list is scrolled: first visible position, its own offset and an enclosing NestedScrollView's. */
fun MainActivity.scrollDescription(spec: ListSpec, title: String? = null): String {
    val list = findViewById<RecyclerView>(spec.listId)
    val layoutManager = list?.layoutManager as? androidx.recyclerview.widget.LinearLayoutManager
    val outer = generateSequence(list?.parent) { it.parent }.filterIsInstance<View>()
        .firstOrNull { it is androidx.core.widget.NestedScrollView }
    return "first visible position ${layoutManager?.findFirstVisibleItemPosition()}, " +
        "list scrollY ${list?.computeVerticalScrollOffset()}, outer scroll ${outer?.scrollY}" +
        (outer?.let { " of ${((it as android.view.ViewGroup).getChildAt(0)?.height ?: 0) - it.height}" } ?: "") +
        (title?.let { wanted ->
            val titles = (0 until (list?.childCount ?: 0)).map { i ->
                list.getChildAt(i).findViewById<TextView>(spec.titleId)?.text?.toString()
            }
            val index = titles.indexOf(wanted)
            val child = list?.getChildAt(index)
            val shown = generateSequence<View>(child) { it.parent as? View }.map { "${it.javaClass.simpleName}:${it.visibility}/${it.alpha}" }.take(4).joinToString(">")
            ", \"$wanted\" is child $index of ${titles.size} at y ${child?.let { c -> IntArray(2).also(c::getLocationOnScreen)[1] }}" +
                " ($shown), first titles ${titles.take(4)}"
        } ?: "")
}

fun favoriteCard(spec: ListSpec, title: String) {
    onView(cardFavorite(spec, title)).perform(click())
}

fun assertCardDisplayed(spec: ListSpec, title: String) {
    onView(cardTitle(spec, title)).check(matches(isDisplayed()))
}

/**
 * Waits for the card titled [title] to be on screen (a return transition may still be running);
 * on timeout, says which list position is showing instead.
 */
fun ActivityScenario<MainActivity>.awaitCardOnScreen(
    spec: ListSpec,
    title: String,
    before: String = "",
    timeoutMillis: Long = 5_000,
) {
    val deadline = System.currentTimeMillis() + timeoutMillis
    while (true) {
        instrumentation.waitForIdleSync()
        var onScreen = false
        var where = ""
        onActivity { activity ->
            val list = activity.findViewById<RecyclerView>(spec.listId)
            val titleView = (0 until (list?.childCount ?: 0)).asSequence()
                .mapNotNull { list.getChildAt(it).findViewById<TextView>(spec.titleId) }
                .firstOrNull { it.text?.toString() == title }
            onScreen = titleView?.getGlobalVisibleRect(Rect()) == true
            where = activity.scrollDescription(spec, title)
        }
        if (onScreen) return
        if (System.currentTimeMillis() > deadline) fail("\"$title\" isn't on screen after coming back: $where (before: $before)")
        Thread.sleep(100)
    }
}

fun assertActivityToolbarHidden() {
    onView(withId(R.id.my_toolbar)).check(matches(not(isDisplayed())))
}

/**
 * Taps the menu item [itemId] of [toolbarId] (the activity toolbar by default): as an action
 * button when it's shown, otherwise from the overflow menu by its title.
 */
fun ActivityScenario<MainActivity>.clickToolbarItem(@IdRes itemId: Int, @IdRes toolbarId: Int = R.id.my_toolbar) {
    // After navigating, the toolbar menu is rebuilt a moment later (a single try missed it):
    // wait for the item, shown either as an action button or kept for the overflow menu
    var asAction = false
    var title: CharSequence? = null
    waitUntil("menu item $itemId in the toolbar", timeoutMillis = 5_000) { activity ->
        val toolbar = activity.findViewById<Toolbar>(toolbarId) ?: return@waitUntil false
        val item = toolbar.menu.findItem(itemId) ?: return@waitUntil false
        title = item.title
        asAction = toolbar.findViewById<View>(itemId)?.isShown == true
        asAction || !item.isActionViewExpanded && item.isVisible && toolbar.findViewById<View>(itemId) == null
    }
    if (asAction) {
        // The menu can be rebuilt between finding the button and tapping it: try again briefly
        val deadline = System.currentTimeMillis() + 5_000
        while (true) {
            try {
                onView(allOf(withId(itemId), isDescendantOfA(withId(toolbarId)), isDisplayed())).perform(click())
                return
            } catch (e: androidx.test.espresso.NoMatchingViewException) {
                if (System.currentTimeMillis() > deadline) throw e
                Thread.sleep(100)
            }
        }
    }
    if (toolbarId == R.id.my_toolbar) {
        openActionBarOverflowOrOptionsMenu(instrumentation.targetContext)
    } else {
        onActivity { it.findViewById<Toolbar>(toolbarId).showOverflowMenu() }
    }
    onView(withText(title.toString())).perform(click())
}

/** Matches only the first view [matcher] matches, so repeated rows don't make Espresso fail. */
fun <T> first(matcher: Matcher<T>): Matcher<T> = object : TypeSafeMatcher<T>() {
    private var matched = false
    override fun describeTo(description: Description) {
        description.appendText("first matching: ").appendDescriptionOf(matcher)
    }

    override fun matchesSafely(item: T): Boolean {
        if (!matched && matcher.matches(item)) {
            matched = true
            return true
        }
        return false
    }
}
