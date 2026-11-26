package com.rick.ecatalogs.benchmarks

import android.Manifest.permission
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

/**
 * MainActivity asks for the notification permission once; granting it up front keeps that
 * dialog from covering the screens being profiled.
 */
fun MacrobenchmarkScope.allowNotifications() {
    if (SDK_INT >= TIRAMISU) {
        device.executeShellCommand("pm grant $packageName ${permission.POST_NOTIFICATIONS}")
    }
}

/**
 * Opens the app on the Trending Series catalog. Each run installs a fresh, signed-out app, so a
 * plain launch would stop at the sign-in screen: this opens the catalog's deep link directly,
 * the way a notification does.
 */
fun MacrobenchmarkScope.startActivityAndAllowNotifications() {
    allowNotifications()
    startActivityAndWait(
        Intent(Intent.ACTION_VIEW, Uri.parse(CATALOG_DEEP_LINK)).apply {
            component = ComponentName(packageName, MAIN_ACTIVITY)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        },
    )
}

private const val CATALOG_DEEP_LINK = "com.rick.ecs://trending_series_catalog"
private const val MAIN_ACTIVITY = "com.rick.ecatalogs.MainActivity"

/**
 * Opens a screen from the navigation drawer by its English menu title, e.g. "Anime".
 * The device language must be English.
 */
fun MacrobenchmarkScope.openFromDrawer(title: String) {
    // The toolbar's drawer button (Navigation UI's content description)
    val drawerButton = By.desc("Open navigation drawer")
    if (device.wait(Until.hasObject(drawerButton), 5_000)) {
        device.findObject(drawerButton).click()
    } else {
        // Swipe in from the left edge
        device.swipe(0, device.displayHeight / 2, device.displayWidth / 2, device.displayHeight / 2, 10)
    }
    val drawer = device.waitAndFindObject(res("nav_view"), 5_000)
    drawer.wait(Until.hasObject(By.text(title)), 5_000)
    drawer.findObject(By.text(title)).click()
    device.wait(Until.gone(res("nav_view")), 5_000)
    device.waitForIdle()
}
