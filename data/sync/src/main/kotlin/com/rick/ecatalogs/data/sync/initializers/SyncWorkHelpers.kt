package com.rick.ecatalogs.data.sync.initializers

import androidx.work.Constraints
import androidx.work.NetworkType

// All sync work needs an internet connection
internal val SyncConstraints
    get() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
