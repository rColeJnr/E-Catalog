package com.rick.ecatalogs.data.sync.status

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.rick.ecatalogs.data.sync.initializers.PERIODIC_SYNC_WORK_NAME
import com.rick.ecatalogs.data.sync.initializers.SYNC_WORK_NAME
import com.rick.ecatalogs.data.sync.workers.SyncWorker
import com.rick.ecatalogs.data.util.SyncManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import javax.inject.Inject

/**
 * [SyncManager] backed by [WorkInfo] from [WorkManager]
 */
internal class WorkManagerSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : SyncManager {
    override val isSyncing: Flow<Boolean> =
        WorkManager.getInstance(context).run {
            combine(
                getWorkInfosForUniqueWorkFlow(SYNC_WORK_NAME),
                getWorkInfosForUniqueWorkFlow(PERIODIC_SYNC_WORK_NAME),
            ) { oneTime, periodic -> (oneTime + periodic).anyRunning() }
        }.conflate()

    override fun requestSync() {
        val workManager = WorkManager.getInstance(context)
        // Ensure only one sync worker runs at any time
        workManager.enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            SyncWorker.startUpSyncWork(),
        )
    }

    override fun setPeriodicSync(enabled: Boolean, intervalHours: Int) {
        val workManager = WorkManager.getInstance(context)
        if (enabled) {
            workManager.enqueueUniquePeriodicWork(
                PERIODIC_SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                SyncWorker.periodicSyncWork(intervalHours),
            )
        } else {
            workManager.cancelUniqueWork(PERIODIC_SYNC_WORK_NAME)
        }
    }
}

private fun List<WorkInfo>.anyRunning() = any { it.state == WorkInfo.State.RUNNING }
