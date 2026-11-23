package com.rick.ecatalogs.data.ui_design

import android.net.Uri
import android.view.View
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.navOptions

/**
 * App-wide screen transition (Material shared axis X). The same animations are set on every
 * `<action>` in the nav graphs; deep-link navigation goes through [navigateWithMotion].
 */
val motionNavOptions: NavOptions = navOptions {
    anim {
        enter = R.anim.data_ui_design_nav_enter
        exit = R.anim.data_ui_design_nav_exit
        popEnter = R.anim.data_ui_design_nav_pop_enter
        popExit = R.anim.data_ui_design_nav_pop_exit
    }
}

fun NavController.navigateWithMotion(uri: Uri, extras: Navigator.Extras? = null) =
    navigateWithMotion(NavDeepLinkRequest.Builder.fromUri(uri).build(), extras)

fun NavController.navigateWithMotion(request: NavDeepLinkRequest, extras: Navigator.Extras? = null) =
    navigate(request, motionNavOptions, extras)

const val POSTER_KIND_MOVIE = "movie"
const val POSTER_KIND_SERIES = "series"
const val POSTER_KIND_ANIME = "anime"
const val POSTER_KIND_MANGA = "manga"

/** Transition name shared by a list item's poster and the details screen's image. */
fun posterTransitionName(kind: String, id: Any) = "poster_${kind}_$id"

/**
 * Navigates to [uri], morphing [sharedView] into the destination's view with the same transition
 * name. Without a transition name it falls back to the regular screen transition.
 */
fun NavController.navigateWithSharedElement(uri: Uri, sharedView: View?) {
    val name = sharedView?.let(ViewCompat::getTransitionName)
    if (sharedView == null || name == null) {
        navigateWithMotion(uri)
    } else {
        // No screen-wide animation: the container transform is the transition
        navigate(
            NavDeepLinkRequest.Builder.fromUri(uri).build(),
            null,
            FragmentNavigatorExtras(sharedView to name),
        )
    }
}
