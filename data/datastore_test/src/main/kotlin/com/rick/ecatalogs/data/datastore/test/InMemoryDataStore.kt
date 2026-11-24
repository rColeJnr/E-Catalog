package com.rick.ecatalogs.data.datastore.test

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.updateAndGet

/** A [DataStore] kept in memory, for unit tests (same as Now in Android's). */
class InMemoryDataStore<T>(initialValue: T) : DataStore<T> {
    override val data = MutableStateFlow(initialValue)
    override suspend fun updateData(
        transform: suspend (it: T) -> T,
    ) = data.updateAndGet { transform(it) }
}
