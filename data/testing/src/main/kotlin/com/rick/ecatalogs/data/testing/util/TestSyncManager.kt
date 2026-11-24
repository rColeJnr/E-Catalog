package com.rick.ecatalogs.data.testing.util

import com.rick.ecatalogs.data.util.SyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TestSyncManager : SyncManager {

    private val syncStatusFlow = MutableStateFlow(false)

    override val isSyncing: Flow<Boolean> = syncStatusFlow

    /** Number of times [requestSync] was called. */
    var syncRequests = 0
        private set

    /** The last schedule passed to [setPeriodicSync], as (enabled, intervalHours). */
    var periodicSync: Pair<Boolean, Int>? = null
        private set

    override fun requestSync() {
        syncRequests++
    }

    override fun setPeriodicSync(enabled: Boolean, intervalHours: Int) {
        periodicSync = enabled to intervalHours
    }

    /**
     * A test-only API to set the sync status from tests.
     */
    fun setSyncing(isSyncing: Boolean) {
        syncStatusFlow.value = isSyncing
    }
}
