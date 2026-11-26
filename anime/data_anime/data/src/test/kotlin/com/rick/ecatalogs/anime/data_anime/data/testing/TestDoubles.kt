package com.rick.ecatalogs.anime.data_anime.data.testing

import android.content.Context
import android.content.ContextWrapper
import com.rick.ecatalogs.data.datastore.ListCache
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.data.datastore.UserPreferences
import com.rick.ecatalogs.data.datastore.test.InMemoryDataStore
import java.io.File
import java.lang.reflect.Proxy

/**
 * An implementation of interface [T] whose every method fails: delegate a fake to it
 * (`class Fake : Api by unimplemented()`) and override only what the test uses.
 */
inline fun <reified T : Any> unimplemented(): T = Proxy.newProxyInstance(
    T::class.java.classLoader,
    arrayOf(T::class.java),
) { _, method, _ -> throw NotImplementedError("${T::class.simpleName}.${method.name}") } as T

/** A context whose cache directory is [cacheDir] (the repositories keep JSON files there). */
fun testContext(cacheDir: File): Context = object : ContextWrapper(null) {
    override fun getCacheDir(): File = cacheDir
}

fun testListCache() =
    ListCache(PreferencesDataSource(InMemoryDataStore(UserPreferences.getDefaultInstance())))
