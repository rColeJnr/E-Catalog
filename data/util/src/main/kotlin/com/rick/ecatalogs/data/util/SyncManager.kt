package com.rick.ecatalogs.data.util

import kotlinx.coroutines.flow.Flow

/**
 * Reports on if synchronization is in progress, and controls the background sync schedule.
 */
interface SyncManager {
    val isSyncing: Flow<Boolean>
    fun requestSync()

    /**
     * Schedules periodic background sync every [intervalHours] hours, or cancels it when
     * [enabled] is false.
     */
    fun setPeriodicSync(enabled: Boolean, intervalHours: Int)
}
