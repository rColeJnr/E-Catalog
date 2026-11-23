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
            // Keep syncing in the background. UPDATE applies interval/constraint changes on the
            // next launch without restarting the schedule.
            enqueueUniquePeriodicWork(
                PERIODIC_SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                SyncWorker.periodicSyncWork(),
            )
        }
    }
}

// These names should not be changed otherwise the app may have concurrent sync requests running
internal const val SYNC_WORK_NAME = "SyncWorkName"
internal const val PERIODIC_SYNC_WORK_NAME = "PeriodicSyncWorkName"
