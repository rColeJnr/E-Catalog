package com.rick.ecatalogs.data.sync.status

import android.util.Log
import javax.inject.Inject


class StubSyncSubscriber @Inject constructor() : SyncSubscriber {
    override suspend fun subscribe() {
        Log.d(TAG, "Subscribing to sync")
    }
}
private const val TAG = "StubSyncSubscriber"