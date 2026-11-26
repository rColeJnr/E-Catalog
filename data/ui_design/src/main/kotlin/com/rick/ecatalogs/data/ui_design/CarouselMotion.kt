package com.rick.ecatalogs.data.ui_design

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import kotlin.math.abs
import android.os.Build
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView

/**
 * Turns a horizontal row into a centered carousel: the current card sits in the middle at
 * [CAROUSEL_ITEM_WIDTH] of the row's width, with at most one smaller card peeking on each side
 * (none at the first and last card), and swipes snap card by card. Side cards are scaled down
 * and dimmed, growing as they move to the center. Items should be `match_parent` in both
 * directions; the RecyclerView needs a fixed height.
 */
fun RecyclerView.useHeroCarousel() {
    if (layoutManager is CenteredCarouselLayoutManager) return
    layoutManager = CenteredCarouselLayoutManager(this)
    clipToPadding = false
    PagerSnapHelper().attachToRecyclerView(this)
}

/**
 * Fades [views] (titles, badges, buttons) out as this carousel card moves to the side, so text
 * only shows on the centered card.
 */
fun View.fadeWhenSmall(vararg views: View) {
    setTag(R.id.data_ui_design_carousel_fade_views, views.toList())
}

/** Horizontal layout with every card the same width, centered by symmetric padding. */
private class CenteredCarouselLayoutManager(
    private val recyclerView: RecyclerView,
) : LinearLayoutManager(recyclerView.context, HORIZONTAL, false) {

    private fun itemWidth(): Int =
        (width.takeIf { it > 0 } ?: recyclerView.width).let { (it * CAROUSEL_ITEM_WIDTH).toInt() }

    // Every card gets the same width, whatever the item layout says
    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean =
        super.checkLayoutParams(lp) && lp?.width == itemWidth()

    override fun generateDefaultLayoutParams() =
        super.generateDefaultLayoutParams().apply { width = itemWidth() }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams) =
        super.generateLayoutParams(lp).apply { width = itemWidth() }

    override fun generateLayoutParams(c: Context, attrs: AttributeSet) =
        super.generateLayoutParams(c, attrs).apply { width = itemWidth() }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        // Symmetric padding puts the first and last card in the center too
        val side = (width - itemWidth()) / 2
        if (width > 0 && (recyclerView.paddingLeft != side || recyclerView.paddingRight != side)) {
            recyclerView.setPadding(side, recyclerView.paddingTop, side, recyclerView.paddingBottom)
        }
        super.onLayoutChildren(recycler, state)
        applyDepth()
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
    ): Int = super.scrollHorizontallyBy(dx, recycler, state).also { applyDepth() }

    /** Scale, dim and fade text by each card's distance from the center. */
    private fun applyDepth() {
        val center = width / 2f
        val span = itemWidth().takeIf { it > 0 } ?: return
        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: continue
            val childCenter = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2f
            val distance = (abs(childCenter - center) / span).coerceIn(0f, 1f)
            val scale = 1f - (1f - SIDE_SCALE) * distance
            // Side cards shrink towards the centered card, so they hug it instead of leaving a gap
            child.pivotX = if (childCenter < center) child.width.toFloat() else 0f
            child.pivotY = child.height / 2f
            child.scaleX = scale
            child.scaleY = scale
            child.alpha = 1f - (1f - SIDE_ALPHA) * distance
            @Suppress("UNCHECKED_CAST")
            (child.getTag(R.id.data_ui_design_carousel_fade_views) as? List<View>)?.forEach {
                it.alpha = (1f - distance * TEXT_FADE_SPEED).coerceIn(0f, 1f)
            }
        }
    }
}

/**
 * Cards fade, rise and scale in as they scroll into view (from below when scrolling down,
 * from above when scrolling up). Nothing animates on the first layout or when coming back from
 * another screen (the list isn't scrolling then), so shared-element transitions aren't
 * disturbed; nothing animates when the system's animations are turned off.
 */
fun RecyclerView.animateItemsOnScroll() {
    var direction = 1
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy != 0) direction = if (dy > 0) 1 else -1
        }
    })
    addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) {
            if (scrollState == RecyclerView.SCROLL_STATE_IDLE || !animationsEnabled()) return
            view.alpha = 0f
            view.translationY = ENTER_OFFSET_DP * resources.displayMetrics.density * direction
            view.scaleX = ENTER_SCALE
            view.scaleY = ENTER_SCALE
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(ENTER_DURATION_MILLIS)
                .setInterpolator(DecelerateInterpolator(1.6f))
                .start()
        }

        override fun onChildViewDetachedFromWindow(view: View) {
            // Recycled views must come back in their normal state
            view.animate().cancel()
            view.alpha = 1f
            view.translationY = 0f
            view.scaleX = 1f
            view.scaleY = 1f
        }
    })
}

private fun animationsEnabled() =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.O || ValueAnimator.areAnimatorsEnabled()

/** Width of each card as a fraction of the row: leaves room for one neighbour per side. */
private const val CAROUSEL_ITEM_WIDTH = 0.72f
private const val SIDE_SCALE = 0.84f
private const val SIDE_ALPHA = 0.75f
private const val TEXT_FADE_SPEED = 2.5f
private const val ENTER_OFFSET_DP = 48
private const val ENTER_SCALE = 0.94f
private const val ENTER_DURATION_MILLIS = 380L
