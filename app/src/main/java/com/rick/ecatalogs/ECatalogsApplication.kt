package com.rick.ecatalogs

import android.app.Application
import com.rick.ecatalogs.data.sync.initializers.Sync
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ECatalogsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Sync; the system responsible for keeping data in the app up to date.
        Sync.initialize(context = this)
    }
}
