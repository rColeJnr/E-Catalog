package com.rick.ecatalogs.book.data_book.data.testing

import com.rick.ecatalogs.data.datastore.ListCache
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.data.datastore.UserPreferences
import com.rick.ecatalogs.data.datastore.test.InMemoryDataStore
import java.lang.reflect.Proxy

/**
 * An implementation of interface [T] whose every method fails: delegate a fake to it
 * (`class Fake : Api by unimplemented()`) and override only what the test uses.
 */
inline fun <reified T : Any> unimplemented(): T = Proxy.newProxyInstance(
    T::class.java.classLoader,
    arrayOf(T::class.java),
) { _, method, _ -> throw NotImplementedError("${T::class.simpleName}.${method.name}") } as T

fun testListCache() =
    ListCache(PreferencesDataSource(InMemoryDataStore(UserPreferences.getDefaultInstance())))
