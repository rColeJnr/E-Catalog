package com.rick.ecatalogs.data.sync.initializers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.rick.ecatalogs.data.sync.workers.SyncWorker

object Sync {
    // Initializes sync, the process that keeps the app's data current.
    // It is called from the app module's Application.onCreate() and should be only done once.
    fun initialize(context: Context) {
        WorkManager.getInstance(context).apply {
            // Run sync on app startup and ensure only one sync worker runs at any time
            enqueueUniqueWork(
                SYNC_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                SyncWorker.startUpSyncWork(),
            )
            // Keep syncing in the background. KEEP so the interval chosen in Settings (applied with
            // SyncManager.setPeriodicSync) isn't reset on every launch. If the user turned sync
            // off, the worker returns early without touching the network.
            enqueueUniquePeriodicWork(
                PERIODIC_SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                SyncWorker.periodicSyncWork(),
            )
        }
    }
}

// These names should not be changed otherwise the app may have concurrent sync requests running
internal const val SYNC_WORK_NAME = "SyncWorkName"
internal const val PERIODIC_SYNC_WORK_NAME = "PeriodicSyncWorkName"
