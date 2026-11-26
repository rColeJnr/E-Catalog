package com.rick.ecatalogs

import com.rick.ecatalogs.data.ui_components.common.installImageCaching
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.rick.ecatalogs.data.sync.initializers.Sync
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ECatalogsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Before any screen loads an image: one disk cache for Glide and Coil
        installImageCaching(this)
        // The app is dark-first. Apply the last chosen mode before any activity is created, so
        // the first frame already uses it (the saved setting is applied again by MainActivity).
        AppCompatDelegate.setDefaultNightMode(
            getSharedPreferences(LAUNCH_PREFS, MODE_PRIVATE)
                .getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_YES)
        )
        // Initialize Sync; the system responsible for keeping data in the app up to date.
        Sync.initialize(context = this)
    }
}
