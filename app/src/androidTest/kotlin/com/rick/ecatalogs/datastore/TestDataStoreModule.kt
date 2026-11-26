package com.rick.ecatalogs.datastore

import androidx.datastore.core.DataStore
import com.rick.ecatalogs.data.datastore.DatastoreModule
import com.rick.ecatalogs.data.datastore.UserPreferences
import com.rick.ecatalogs.data.datastore.test.InMemoryDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * Each test starts from default preferences (no favorites, every list stale, all notifications on),
 * so settings and favorites written by one test never leak into the next.
 */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DatastoreModule::class])
object TestDataStoreModule {
    @Provides
    @Singleton
    fun providesUserPreferencesDataStore(): DataStore<UserPreferences> =
        InMemoryDataStore(UserPreferences.getDefaultInstance())
}
