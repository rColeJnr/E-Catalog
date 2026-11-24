package com.rick.ecatalogs.data.sync.di

import com.rick.ecatalogs.data.sync.status.WorkManagerReleaseReminderScheduler
import com.rick.ecatalogs.data.util.ReleaseReminderScheduler
import com.rick.ecatalogs.data.sync.status.StubSyncSubscriber
import com.rick.ecatalogs.data.sync.status.SyncSubscriber
import com.rick.ecatalogs.data.sync.status.WorkManagerSyncManager
import com.rick.ecatalogs.data.util.SyncManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SyncModule {
    @Binds
    abstract fun bindsSyncStatusMonitor(
        syncStatusMonitor: WorkManagerSyncManager,
    ): SyncManager

    @Binds
    abstract fun bindsReleaseReminderScheduler(
        scheduler: WorkManagerReleaseReminderScheduler,
    ): ReleaseReminderScheduler

    @Binds
    abstract fun bindsSyncSubscriber(
        syncSubscriber: StubSyncSubscriber,
    ): SyncSubscriber
}
